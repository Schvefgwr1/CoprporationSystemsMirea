package test.pr4_server.models;

import jakarta.xml.bind.annotation.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "GameSession")
@XmlAccessorType(XmlAccessType.FIELD)
public class GameSessionLog {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String secretCode;

    @XmlElementWrapper(name = "guesses")
    @XmlElement(name = "guess")
    private List<GuessEntry> guesses;

    private String winner;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GuessEntry {
        private String player;
        private String value;
        private int black;
        private int white;
    }
}

