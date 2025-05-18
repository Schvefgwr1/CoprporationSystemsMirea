package test.pr3_server.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class SaveAnalysisResultException extends Exception {
    private final String path;
    private final String content;

    @Override
    public String toString() {
        return String.format(
                "Error saving analysis result to path: %s with content: %s...",
                this.path,
                this.content.substring(0, Math.min(20, content.length()))
        );
    }
}
