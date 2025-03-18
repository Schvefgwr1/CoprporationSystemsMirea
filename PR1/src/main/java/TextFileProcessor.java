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
        if (args.length < 2) {
            System.out.println("Usage: java TextFileProcessor <file_path> <word>");
            return;
        }

        String filePath = args[0];
        String searchWord = args[1];

        try {
            String content = readFile(filePath);
            int wordCount = countWords(content);
            int occurrences = countOccurrences(content, searchWord);

            System.out.println("Total words in file: " + wordCount);
            System.out.println("Occurrences of '" + searchWord + "': " + occurrences);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Reads the content of a text file.
     *
     * @param filePath Path to the file to be read.
     * @return Content of the file as a string.
     * @throws IOException If an error occurs while reading the file.
     */
    public static String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }

    /**
     * Counts the total number of words in a given text.
     *
     * @param content The text content.
     * @return The number of words in the text.
     */
    public static int countWords(String content) {
        String[] words = getWordsArray(content);
        return words.length;
    }

    /**
     * Counts the occurrences of a specific word in the given text.
     *
     * @param content    The text content.
     * @param searchWord The word to search for.
     * @return The number of times the word appears in the text.
     */
    public static int countOccurrences(String content, String searchWord) {
        String[] words = getWordsArray(content);
        int count = 0;
        for (String word : words) {
            if (word.equalsIgnoreCase(searchWord)) {
                count++;
            }
        }
        return count;
    }

    private static String[] getWordsArray(String content) {
        return content.split("\\s+|[.,!?;:(){}]\\s*");
    }
}