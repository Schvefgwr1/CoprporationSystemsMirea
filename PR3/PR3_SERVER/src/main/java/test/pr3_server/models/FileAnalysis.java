package test.pr3_server.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FileAnalysis {
    private String fileName;
    private int lineCount;
    private int wordCount;
    private int charCount;

    @Override
    public String toString() {
        return String.format("File: %s | Lines: %d, Words: %d, Characters: %d",
                fileName, lineCount, wordCount, charCount);
    }
}

