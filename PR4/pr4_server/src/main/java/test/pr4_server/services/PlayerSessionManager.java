package test.pr4_server.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import test.pr4_server.models.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlayerSessionManager {

    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final Set<String> usedNames = Collections.synchronizedSet(new HashSet<>());

    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;

    public synchronized boolean addPlayer(Player player) {
        if (players.size() >= MAX_PLAYERS || usedNames.contains(player.getName())) {
            return false;
        }
        players.put(player.getId(), player);
        usedNames.add(player.getName());
        return true;
    }

    @Async
    public CompletableFuture<Optional<Player>> asyncAddPlayer(String name) {
        String id = UUID.randomUUID().toString();
        Player player = new Player(id, name);

        boolean added;
        synchronized (this) {
            added = addPlayer(player);
        }

        return CompletableFuture.completedFuture(added ? Optional.of(player) : Optional.empty());
    }

    public synchronized void removePlayer(String playerId) {
        Player player = players.remove(playerId);
        if (player != null) {
            usedNames.remove(player.getName());
        }
    }

    public Optional<Player> getPlayer(String playerId) {
        return Optional.ofNullable(players.get(playerId));
    }

    public Collection<Player> getAllPlayers() {
        return players.values();
    }

    public boolean isReadyToStart() {
        return players.size() >= MIN_PLAYERS;
    }

    public boolean isFull() {
        return players.size() >= MAX_PLAYERS;
    }

    public void reset() {
        players.clear();
        usedNames.clear();
    }

    public int getPlayerCount() {
        return players.size();
    }
}