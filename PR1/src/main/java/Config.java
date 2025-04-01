import lombok.Getter;

/**
 * Class for handling application configuration.
 * Stores file path and search word extracted from command-line arguments.
 */
@Getter
class Config {
    private final String filePath;
    private final String searchWord;

    /**
     * Parses command-line arguments and initializes configuration.
     * @param args Command-line arguments containing file path and search word.
     * @throws IllegalArgumentException If insufficient arguments are provided.
     */
    public Config(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: java Main <file_path> <word>");
        }
        this.filePath = args[0];
        this.searchWord = args[1];
    }
}
