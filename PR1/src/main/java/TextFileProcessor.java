import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A simple text file processor that reads a file, counts the total words,
 * and counts the occurrences of a specific word.
 */
public class TextFileProcessor {
    /**
     * The main entry point of the application.
     *
     * @param args Command-line arguments: <file_path> <word>
     */
    public static void main(String[] args) {
        try {
            Config config = new Config(args);
            FileHandler fileHandler = new FileHandler(config.getFilePath());
            String content = fileHandler.readContent();
            TextAnalyzer analyzer = new TextAnalyzer(content);

            System.out.println("Total words in file: " + analyzer.countWords());
            System.out.println("Occurrences of '" + config.getSearchWord() + "': " + analyzer.countOccurrences(config.getSearchWord()));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}