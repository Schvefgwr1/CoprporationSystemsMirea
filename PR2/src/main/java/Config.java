import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Class for handling application configuration.
 * Stores file paths extracted from command-line arguments.
 */
@Getter
class Config {
    private final List<String> filePaths;

    public Config(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: java Main <file_path1> <file_path2> ...");
        }
        this.filePaths = Arrays.asList(args);
    }
}