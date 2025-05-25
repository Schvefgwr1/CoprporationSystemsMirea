package test.pr4_server.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Player {
    private String id;
    private String name;
    private int attempts;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.attempts = 0;
    }

    public void incrementAttempts() {
        this.attempts++;
    }
}

