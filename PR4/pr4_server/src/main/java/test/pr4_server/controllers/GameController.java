package test.pr4_server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import test.pr4_server.models.Player;
import test.pr4_server.services.GameService;
import test.pr4_server.services.PlayerSessionManager;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final PlayerSessionManager playerManager;
    private final GameService gameService;

    @PostMapping("/join")
    @Async
    public CompletableFuture<ResponseEntity<?>> join(@RequestParam String name) {
        return playerManager.asyncAddPlayer(name)
                .thenCompose(playerOpt -> {
                    if (playerOpt.isEmpty()) {
                        return CompletableFuture.completedFuture(
                                ResponseEntity.badRequest().body(Map.of("error", "Game is full or player already exists"))
                        );
                    }

                    if (playerManager.isReadyToStart() && !gameService.isRoundActive()) {
                        gameService.startNewRound();
                    }

                    Player player = playerOpt.get();
                    return CompletableFuture.completedFuture(
                            ResponseEntity.ok(Map.of("id", player.getId(), "name", player.getName()))
                    );
                });
    }

    @PostMapping("/leave")
    @Async
    public CompletableFuture<ResponseEntity<?>> leave(@RequestParam String playerId) {
        return CompletableFuture.supplyAsync(() -> {
            playerManager.removePlayer(playerId);

            // Если игроков стало меньше минимума и раунд активен
            if (!playerManager.isReadyToStart() && gameService.isRoundActive()) {
                gameService.endRoundEarly("Not enough players");
            }

            return ResponseEntity.ok(Map.of("message", "Successfully left the game"));
        });
    }

    @PostMapping("/guess")
    @Async
    public CompletableFuture<ResponseEntity<?>> guess(
            @RequestParam String playerId,
            @RequestParam String guess
    ) {
        return gameService.asyncMakeGuess(playerId, guess)
                .thenApply(result -> {
                    if (result == null) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Invalid guess or player not found"));
                    }
                    return ResponseEntity.ok(result);
                });
    }

    @GetMapping("/status")
    @Async
    public CompletableFuture<ResponseEntity<?>> status() {
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(Map.of(
                        "players", playerManager.getPlayerCount(),
                        "roundActive", gameService.isRoundActive()
                ))
        );
    }

    @GetMapping("/code-length")
    public ResponseEntity<?> getCodeLength() {
        return ResponseEntity.ok(Map.of("length", gameService.getCurrentCodeLength()));
    }
}