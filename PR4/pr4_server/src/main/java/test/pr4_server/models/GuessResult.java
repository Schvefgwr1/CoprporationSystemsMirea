package test.pr4_server.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuessResult {
    private int black;
    private int white;
    private boolean correct;
    private String error;
}

