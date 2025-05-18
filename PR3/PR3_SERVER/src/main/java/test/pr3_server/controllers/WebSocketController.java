package test.pr3_server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import test.pr3_server.exceptions.SaveAnalysisResultException;
import test.pr3_server.exceptions.SaveClientFileException;
import test.pr3_server.models.FileAnalysis;
import test.pr3_server.models.MessagePayload;
import test.pr3_server.services.FileStorageService;
import test.pr3_server.services.TextAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@AllArgsConstructor
@Slf4j
public class WebSocketController extends TextWebSocketHandler {

    private final FileStorageService fileStorageService;
    private final TextAnalyzer analyzer;

    private TextMessage errorMessage(String errorText) {
        return new TextMessage("{\"error\": true, \"message\": \"" + errorText + "\"}");
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        TextMessage response = new TextMessage("");
        FileAnalysis analysis = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            MessagePayload payload = mapper.readValue(message.getPayload(), MessagePayload.class);
            String fileName = fileStorageService.saveFile(payload.getFileName(), payload.getContent());
            Path filePath = Path.of(fileName);
            String fileContent = Files.readString(filePath);

            analysis = analyzer.analyze(filePath.getFileName().toString(), fileContent);
            fileStorageService.saveAnalysisResult(filePath.getFileName().toString(), analysis);

            response = new TextMessage(mapper.writeValueAsString(analysis));
        } catch (JsonProcessingException e) {
            log.error("Incorrect message: {}, error: {}", message.getPayload(), e.getMessage());
            response = errorMessage("Incorrect input message");
        } catch (SaveClientFileException e) {
            log.error(e.getMessage());
            response = errorMessage("Error of saving client file");
        } catch (SaveAnalysisResultException e) {
            log.error(e.getMessage());
            if(analysis != null) {
                try {
                    response = new TextMessage(mapper.writeValueAsString(analysis));
                } catch (JsonProcessingException ex) {
                    log.error("Incorrect analysis data to parse to JSON: {}", analysis);
                    response = errorMessage("Incorrect output message");
                }
            } else {
                response = errorMessage("Error of saving analysis results");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            response = errorMessage("Error of getting content from client file after saving");
        } catch (Exception e) {
            log.error(e.getMessage());
            response = errorMessage("Unsupported exception: " + e.getMessage());
        }
        finally {
            try {
                session.sendMessage(response);
            } catch (IOException ex) {
                log.error("Unexpected error of sockets in controller: {}", ex.getMessage());
            }
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        log.warn("Received unexpected binary message. Closing session.");
        try {
            session.sendMessage(errorMessage("Binary messages are not supported. Send only .txt files."));
            session.close();
        } catch (IOException e) {
            log.error("Error while closing session after binary message: {}", e.getMessage());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error: {}", exception.getMessage(), exception);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
