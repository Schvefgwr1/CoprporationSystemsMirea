import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data structure for storing file analysis results.
 */
@AllArgsConstructor
class FileAnalysis {
    @Getter
    private final String fileName;
    private final int wordCount;
    private final int charCount;

    @Override
    public String toString() {
        return fileName + ": " + wordCount + " words, " + charCount + " characters";
    }
}
