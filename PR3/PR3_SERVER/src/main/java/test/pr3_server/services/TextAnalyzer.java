package test.pr3_server.services;

import org.springframework.stereotype.Service;
import test.pr3_server.models.FileAnalysis;

@Service
public class TextAnalyzer {

    public FileAnalysis analyze(String fileName, String content) {
        int lines = content.split("\r\n|\r|\n").length;
        int words = content.trim().split("\\s+").length;
        int characters = content.length();

        return new FileAnalysis(fileName, lines, words, characters);
    }
}

