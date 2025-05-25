package test.pr4_server.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoundEndMessage {
    private String winnerID;
    private String winnerName;
    private String secretCode;
    private int attempts;
    private String type = "ROUND_END";
}