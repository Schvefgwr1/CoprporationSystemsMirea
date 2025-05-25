package test.pr4_server.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import test.pr4_server.controllers.GameWebSocketHandler;
import test.pr4_server.models.GameSessionLog;
import test.pr4_server.models.GuessResult;
import test.pr4_server.models.Player;
import test.pr4_server.models.RoundEndMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class GameService {

    private final PlayerSessionManager playerManager;
    private final AtomicBoolean roundActive = new AtomicBoolean(false);
    private String secretCode;

    private final GameLogService gameLogService;

    private final List<GameSessionLog.GuessEntry> guesses = new ArrayList<>();
    private LocalDateTime roundStartTime;


    @Getter
    private int currentCodeLength;
    private static final List<Character> CODE_SYMBOLS = List.of(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    );

    public synchronized void startNewRound() {
        if (roundActive.get()) return;

        secretCode = generateSecretCode();

        roundStartTime = LocalDateTime.now();
        guesses.clear();

        roundActive.set(true);
        System.out.println("New round started. Secret: " + secretCode);
    }

    public synchronized void endRoundEarly(String reason) {
        if (roundActive.compareAndSet(true, false)) {
            var log = new GameSessionLog(
                    roundStartTime,
                    LocalDateTime.now(),
                    secretCode,
                    new ArrayList<>(guesses),
                    "Game ended early: " + reason
            );
            gameLogService.saveLog(log);
            guesses.clear();
        }
    }


    public boolean isRoundActive() {
        return roundActive.get();
    }

    @Async
    public CompletableFuture<GuessResult> asyncMakeGuess(String playerId, String guess) {
        Optional<Player> playerOpt = playerManager.getPlayer(playerId);
        if (playerOpt.isEmpty() || !roundActive.get()) {
            return CompletableFuture.completedFuture(null);
        }

        Player player = playerOpt.get();
        GuessResult result = checkGuess(guess.toUpperCase(), player);
        return CompletableFuture.completedFuture(result);
    }

    private GuessResult checkGuess(String guess, Player player) {
        int black = 0;
        int white = 0;

        boolean[] codeUsed = new boolean[currentCodeLength];
        boolean[] guessUsed = new boolean[currentCodeLength];

        for (int i = 0; i < currentCodeLength; i++) {
            if (guess.charAt(i) == secretCode.charAt(i)) {
                black++;
                codeUsed[i] = true;
                guessUsed[i] = true;
            }
        }

        for (int i = 0; i < currentCodeLength; i++) {
            if (guessUsed[i]) continue;
            for (int j = 0; j < currentCodeLength; j++) {
                if (!codeUsed[j] && guess.charAt(i) == secretCode.charAt(j)) {
                    white++;
                    codeUsed[j] = true;
                    break;
                }
            }
        }

        player.incrementAttempts();
        boolean correct = black == currentCodeLength;

        guesses.add(new GameSessionLog.GuessEntry(player.getName(), guess, black, white));

        if (correct) {
            roundActive.compareAndSet(true, false);

            // Сохраняем лог
            var log = new GameSessionLog(
                    roundStartTime,
                    LocalDateTime.now(),
                    secretCode,
                    new ArrayList<>(guesses),
                    player.getName()
            );
            gameLogService.saveLog(log);

            // Отправляем WebSocket сообщение всем клиентам
            var message = new RoundEndMessage(
                    player.getName(),
                    player.getName(),
                    secretCode,
                    player.getAttempts(),
                    "ROUND_END"
            );
            GameWebSocketHandler.broadcastGameUpdate(message);

            // Если есть достаточно игроков, начинаем новый раунд
            if (playerManager.isReadyToStart()) {
                startNewRound();
            }
        }

        return new GuessResult(black, white, correct,
                correct ? "Correct! Player " + player.getName() + " guessed the code in " + player.getAttempts() + " attempts."
                        : "Try again");
    }

    private String generateSecretCode() {
        Random rand = new Random();
        currentCodeLength = rand.nextInt(8) + 3; // [3, 10]
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < currentCodeLength; i++) {
            code.append(CODE_SYMBOLS.get(rand.nextInt(CODE_SYMBOLS.size())));
        }
        return code.toString();
    }
}
