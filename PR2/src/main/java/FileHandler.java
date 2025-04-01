import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Class for handling file operations.
 * Reads file content and checks for supported file formats.
 */
@Getter
class FileHandler implements FileHandlerInterface {
    private final static Set<String> SupportedFiles = Set.of("txt", "docx");
    private final Path filePath;

    public FileHandler(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    public String readContent() throws IOException {
        if (isFileSupported()) {
            return Files.readString(filePath);
        } else {
            throw new IOException("Unsupported file type");
        }
    }

    @Override
    public String getFileName() {
        return filePath.getFileName().toString();
    }

    private boolean isFileSupported() {
        String fileName = filePath.getFileName().toString();
        int lastIndex = fileName.lastIndexOf('.');
        String fileExtension = (lastIndex > 0) ? fileName.substring(lastIndex + 1) : "";
        return SupportedFiles.contains(fileExtension);
    }
}
