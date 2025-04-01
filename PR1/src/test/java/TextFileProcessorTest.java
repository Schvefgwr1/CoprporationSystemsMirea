import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class TextAnalyzerTest {

    @Test
    void testCountOccurrences() {
        TextAnalyzer analyzer = new TextAnalyzer("Java is great. Java is powerful. Java is popular.");
        assertEquals(3, analyzer.countOccurrences("Java"));
        assertEquals(1, analyzer.countOccurrences("great"));
        assertEquals(0, analyzer.countOccurrences("Python"));
    }

    @Test
    void testCountWords() {
        TextAnalyzer analyzer = new TextAnalyzer("Java is great. Java is powerful. Java is popular.");
        assertEquals(9, analyzer.countWords());
    }
}

class ConfigTest {
    @Test
    void testValidConfig() {
        Config config = new Config(new String[]{"file.txt", "Java"});
        assertEquals("file.txt", config.getFilePath());
        assertEquals("Java", config.getSearchWord());
    }

    @Test
    void testInvalidConfig() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Config(new String[]{}));
        assertEquals("Usage: java Main <file_path> <word>", exception.getMessage());
    }
}

class FileHandlerTest {

    @Test
    void testFileNotFound() {
        FileHandler fileHandler = new FileHandler("non_existent_file.txt");
        assertThrows(IOException.class, fileHandler::readContent);
    }

    @Test
    void testUnsupportedFileFormat() {
        FileHandler fileHandler = new FileHandler("document.pdf");
        assertThrows(IOException.class, fileHandler::readContent);
    }

    @Test
    void testSuccessfulFileRead() throws IOException {
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "Hello world!");
        FileHandler fileHandler = new FileHandler(tempFile.toString());

        assertEquals("Hello world!", fileHandler.readContent());

        Files.deleteIfExists(tempFile);
    }

    @Test
    void testEmptyFile() throws IOException {
        Path tempFile = Files.createTempFile("empty", ".txt");
        FileHandler fileHandler = new FileHandler(tempFile.toString());

        assertEquals("", fileHandler.readContent());

        Files.deleteIfExists(tempFile);
    }
}

