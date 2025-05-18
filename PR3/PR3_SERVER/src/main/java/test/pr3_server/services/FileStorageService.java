package test.pr3_server.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import test.pr3_server.exceptions.SaveAnalysisResultException;
import test.pr3_server.exceptions.SaveClientFileException;
import test.pr3_server.models.FileAnalysis;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadedFilesPath;
    private final Path analysisResultsPath;

    public FileStorageService(@Value("${file.storage.root}") String rootDir) throws IOException {
        this.uploadedFilesPath = Paths.get(rootDir, "uploaded_files");
        this.analysisResultsPath = Paths.get(rootDir, "analysis_results");

        Files.createDirectories(uploadedFilesPath);
        Files.createDirectories(analysisResultsPath);
    }

    public String saveFile(String originalName, String base64Content) throws SaveClientFileException {
        String uniqueName = UUID.randomUUID() + "_" + originalName;
        Path path = uploadedFilesPath.resolve(uniqueName);
        byte[] data = Base64.getDecoder().decode(base64Content);
        try {
            Files.write(path, data);
        } catch (IOException e) {
            throw new SaveClientFileException(path.toString(), base64Content);
        }
        return path.toString();
    }

    public void saveAnalysisResult(String fileClientName, FileAnalysis analysis) throws SaveAnalysisResultException {
        String fileName = fileClientName + "_analysis.txt";
        Path resultPath = analysisResultsPath.resolve(fileName);
        try {
            Files.writeString(resultPath, analysis.toString(), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new SaveAnalysisResultException(resultPath.toString(), analysis.toString());
        }
    }
}
