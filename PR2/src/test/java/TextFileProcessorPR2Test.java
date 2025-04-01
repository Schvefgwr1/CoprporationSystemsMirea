import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TextFileProcessorPR2Test {

    @TempDir
    Path tempDir;

    private Path validTxtFile;
    private Path invalidFile;
    private FileHandler fileHandler;

    @BeforeEach
    void setUp() throws IOException {
        validTxtFile = tempDir.resolve("test.txt");
        Files.writeString(validTxtFile, "Hello world! This is a test.");

        invalidFile = tempDir.resolve("test.exe");
        Files.writeString(invalidFile, "Binary data");
    }

    @Test
    void testConfig_ValidArgs_ShouldInitializeFilePaths() {
        String[] args = {"file1.txt", "file2.txt"};
        Config config = new Config(args);
        assertEquals(List.of("file1.txt", "file2.txt"), config.getFilePaths());
    }

    @Test
    void testConfig_NoArgs_ShouldThrowException() {
        String[] args = {};
        assertThrows(IllegalArgumentException.class, () -> new Config(args));
    }

    @Test
    void testFileHandler_ReadContent_Success() throws IOException {
        fileHandler = new FileHandler(validTxtFile.toString());
        assertEquals("Hello world! This is a test.", fileHandler.readContent());
    }

    @Test
    void testFileHandler_ReadContent_UnsupportedFile_ShouldThrowException() {
        fileHandler = new FileHandler(invalidFile.toString());
        assertThrows(IOException.class, fileHandler::readContent);
    }

    @Test
    void testTextAnalyzer_CountWordsAndCharacters() {
        TextAnalyzer analyzer = new TextAnalyzer("Hello world! This is a test.");
        assertEquals(6, analyzer.countWords());
        assertEquals(28, analyzer.countCharacters());
    }

    @Test
    void testProcessFileAsync_ShouldProcessFile() throws IOException, InterruptedException {
        FileHandlerInterface mockFileHandler = Mockito.mock(FileHandlerInterface.class);
        when(mockFileHandler.readContent()).thenReturn("Test content here.");
        when(mockFileHandler.getFileName()).thenReturn("mockfile.txt");

        FileProcessor fileProcessor = new FileProcessor();
        fileProcessor.processFileAsync(mockFileHandler);

        // Подождем выполнения потока
        Thread.sleep(100);

        assertEquals(3, fileProcessor.getTotalWords());
        assertEquals(18, fileProcessor.getTotalCharacters());
    }

    @Test
    void testProcessFiles_ShouldProcessMultipleFiles() throws IOException, InterruptedException {
        // Мокаем три разных файла
        FileHandlerInterface mockFile1 = Mockito.mock(FileHandlerInterface.class);
        when(mockFile1.readContent()).thenReturn("Hello world.");
        when(mockFile1.getFileName()).thenReturn("mock1.txt");

        FileHandlerInterface mockFile2 = Mockito.mock(FileHandlerInterface.class);
        when(mockFile2.readContent()).thenReturn("Java is awesome.");
        when(mockFile2.getFileName()).thenReturn("mock2.txt");

        FileHandlerInterface mockFile3 = Mockito.mock(FileHandlerInterface.class);
        when(mockFile3.readContent()).thenReturn("Mockito helps testing.");
        when(mockFile3.getFileName()).thenReturn("mock3.txt");

        FileProcessor fileProcessor = new FileProcessor();

        // Обрабатываем файлы в отдельных потоках
        fileProcessor.processFileAsync(mockFile1);
        fileProcessor.processFileAsync(mockFile2);
        fileProcessor.processFileAsync(mockFile3);

        // Подождем выполнения потоков
        Thread.sleep(300);

        // Проверяем, что все файлы обработаны
        assertEquals(3, fileProcessor.getResults().size());

        // Проверяем конкретные файлы
        List<FileAnalysis> results = fileProcessor.getResults();
        assertEquals(
                "mock1.txt: 2 words, 12 characters",
                results.stream()
                        .filter(analysis -> analysis.getFileName().equals("mock1.txt"))
                        .findFirst().orElseThrow().toString()
        );
        assertEquals(
                "mock2.txt: 3 words, 16 characters",
                results.stream()
                        .filter(analysis -> analysis.getFileName().equals("mock2.txt"))
                        .findFirst().orElseThrow().toString()
        );
        assertEquals(
                "mock3.txt: 3 words, 22 characters",
                results.stream()
                        .filter(analysis -> analysis.getFileName().equals("mock3.txt"))
                        .findFirst().orElseThrow().toString()
        );

        // Проверяем глобальные результаты
        assertEquals(8, fileProcessor.getTotalWords()); // 2 + 3 + 3
        assertEquals(50, fileProcessor.getTotalCharacters()); // 12 + 16 + 22
    }
}
