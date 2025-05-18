package test.pr3_server.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class SaveClientFileException extends Exception {
    private final String path;
    private final String base64Content;

    @Override
    public String toString() {
        return String.format(
                "Error of saving client file in path: %s with content: %s...",
                this.path,
                this.base64Content.substring(0, Math.min(20, base64Content.length()))
        );
    }
}
