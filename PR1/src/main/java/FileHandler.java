import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for handling file operations.
 * Reads file content and checks for supported file formats.
 */
class FileHandler {
    private final static HashSet<String> SupportedFiles = new HashSet<>(Set.of("txt", "docx"));
    private final Path filePath;

    /**
     * Constructor to initialize file path.
     * @param filePath The path of the file.
     */
    public FileHandler(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Reads the content of the file if it is supported.
     * @return The file content as a string.
     * @throws IOException If the file is not supported or cannot be read.
     */
    public String readContent() throws IOException {
        if (isFileSupported()) {
            return Files.readString(filePath);
        } else {
            throw new IOException("Unsupported file type");
        }
    }

    /**
     * Checks if the file format is supported.
     * @return True if the file format is supported, otherwise false.
     */
    private boolean isFileSupported() {
        String fileName = filePath.getFileName().toString();
        String fileExtension = "";

        // Extracting file extension
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex > 0) {
            fileExtension = fileName.substring(lastIndex + 1);
        }

        return SupportedFiles.contains(fileExtension);
    }
}