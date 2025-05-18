package test.pr3_server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import test.pr3_server.models.FileAnalysis;
import test.pr3_server.services.FileStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class FileStorageServiceTest {
    @TempDir
    Path tempDir;

    private FileStorageService service;

    @BeforeEach
    void setup() throws IOException {
        service = new FileStorageService(tempDir.toString());
    }

    @Test
    void saveFile_shouldSaveDecodedContent() throws Exception {
        String content = "Test content";
        String base64 = Base64.getEncoder().encodeToString(content.getBytes());

        String path = service.saveFile("test.txt", base64);

        assertTrue(Files.exists(Path.of(path)));
        assertEquals(content, Files.readString(Path.of(path)));
    }

    @Test
    void saveFile_shouldThrowOnInvalidBase64() {
        assertThrows(IllegalArgumentException.class, () ->
                service.saveFile("test.txt", "not_base64"));
    }

    @Test
    void saveAnalysisResult_shouldCreateFileWithCorrectContent() throws Exception {
        FileAnalysis analysis = new FileAnalysis("file.txt", 1, 2, 3);
        service.saveAnalysisResult("file.txt", analysis);

        Path expectedFile = tempDir.resolve("analysis_results").resolve("file.txt_analysis.txt");
        assertTrue(Files.exists(expectedFile));
        assertTrue(Files.readString(expectedFile).contains("Lines: 1"));
    }

}
