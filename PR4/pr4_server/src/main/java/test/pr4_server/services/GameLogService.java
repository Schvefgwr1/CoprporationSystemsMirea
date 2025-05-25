package test.pr4_server.services;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import test.pr4_server.models.GameSessionLog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

@Service
public class GameLogService {
    private static final String LOG_DIR = "game-logs";
    private final String rootDir;

    public GameLogService(@Value("${file.storage.root}") String rootDir) {
       this.rootDir = rootDir;
    }

    public void saveLog(GameSessionLog log) {
        try {
            Files.createDirectories(Path.of(rootDir + "/" + LOG_DIR));

            String timestamp = log.getEndTime().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File file = new File(rootDir + "/" + LOG_DIR + "/session_" + timestamp + ".xml");

            JAXBContext context = JAXBContext.newInstance(GameSessionLog.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(log, file);

            System.out.println("Game session saved to: " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
