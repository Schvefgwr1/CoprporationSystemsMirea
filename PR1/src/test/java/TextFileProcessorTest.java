import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TextFileProcessor class.
 */
class TextFileProcessorTest {
    /**
     * Tests the countOccurrences method with various cases.
     */
    @Test
    void testCountOccurrences() {
        String text = "Java is great. Java is powerful. Java is popular.";
        assertEquals(3, TextFileProcessor.countOccurrences(text, "Java"));
        assertEquals(1, TextFileProcessor.countOccurrences(text, "great"));
        assertEquals(0, TextFileProcessor.countOccurrences(text, "Python"));
    }

    /**
     * Tests the countWords method to ensure it accurately counts words.
     */
    @Test
    void testCountWords() {
        String text = "Java is great. Java is powerful. Java is popular.";
        assertEquals(9, TextFileProcessor.countWords(text));
    }

    /**
     * Tests behavior when no arguments are provided.
     */
    @Test
    void testNoArgumentsProvided() {
        String[] args = {};
        TextFileProcessor.main(args);
        // Expected: Should print usage message and not crash
    }

    /**
     * Tests behavior when the file is not found.
     */
    @Test
    void testFileNotFound() {
        String[] args = {"non_existent_file.txt", "Java"};
        TextFileProcessor.main(args);
        // Expected: Should print error message and not crash
    }
}
