import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FileProcessor {
    private final AtomicInteger totalWords = new AtomicInteger(0);
    private final AtomicInteger totalCharacters = new AtomicInteger(0);

    @Getter
    private final List<FileAnalysis> results = new CopyOnWriteArrayList<>();

    private final AtomicInteger remainingTasks = new AtomicInteger(0);
    private final Object lock = new Object();

    public void processFiles(List<String> filePaths) {
        remainingTasks.set(filePaths.size());

        for (String filePath : filePaths) {
            processFileAsync(new FileHandler(filePath));
        }

        synchronized (lock) {
            while (remainingTasks.get() > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void processFileAsync(FileHandlerInterface fileHandler) {
        new Thread(() -> {
            try {
                String content = fileHandler.readContent();
                TextAnalyzer analyzer = new TextAnalyzer(content);
                int words = analyzer.countWords();
                int chars = analyzer.countCharacters();

                totalWords.addAndGet(words);
                totalCharacters.addAndGet(chars);
                results.add(new FileAnalysis(fileHandler.getFileName(), words, chars));
            } catch (IOException e) {
                System.err.println("Error reading file " + fileHandler.getFileName() + ": " + e.getMessage());
            } finally {
                synchronized (lock) {
                    remainingTasks.decrementAndGet();
                    if (remainingTasks.get() == 0) {
                        lock.notify();
                    }
                }
            }
        }).start();
    }

    public int getTotalWords() {
        return totalWords.get();
    }

    public int getTotalCharacters() {
        return totalCharacters.get();
    }

}
