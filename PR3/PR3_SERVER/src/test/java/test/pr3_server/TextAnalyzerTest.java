package test.pr3_server;

import org.junit.jupiter.api.Test;
import test.pr3_server.models.FileAnalysis;
import test.pr3_server.services.TextAnalyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextAnalyzerTest {

    private final TextAnalyzer analyzer = new TextAnalyzer();

    @Test
    void analyze_shouldReturnCorrectCounts() {
        String content = "Hello world\nThis is a test";
        FileAnalysis result = analyzer.analyze("file.txt", content);

        assertEquals(2, result.getLineCount());
        assertEquals(6, result.getWordCount());
        assertEquals(content.length(), result.getCharCount());
    }


    @Test
    void analyze_emptyContent_shouldReturnZeroCounts() {
        FileAnalysis result = analyzer.analyze("file.txt", "");
        assertEquals(1, result.getLineCount()); // 1 empty line
        assertEquals(1, result.getWordCount());
        assertEquals(0, result.getCharCount());
    }
}
