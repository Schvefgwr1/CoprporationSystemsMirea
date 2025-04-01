public class TextFileProcessorPR2 {
    public static void main(String[] args) {
        Config config = new Config(args);
        FileProcessor fileProcessor = new FileProcessor();
        fileProcessor.processFiles(config.getFilePaths());

        System.out.println("Analysis Results:");
        fileProcessor.getResults().forEach(System.out::println);
        System.out.println("Total: " + fileProcessor.getTotalWords() + " words, " + fileProcessor.getTotalCharacters() + " characters.");
    }
}
