package test.pr3_server.controllers_tests;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.web.socket.TextMessage;
import test.pr3_server.controllers.WebSocketController;
import test.pr3_server.exceptions.SaveAnalysisResultException;
import test.pr3_server.exceptions.SaveClientFileException;
import test.pr3_server.models.FileAnalysis;
import test.pr3_server.models.MessagePayload;
import test.pr3_server.services.FileStorageService;
import test.pr3_server.services.TextAnalyzer;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class WebSocketControllerTest {

    private FileStorageService fileStorageService;
    private TextAnalyzer textAnalyzer;
    private WebSocketController controller;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        fileStorageService = mock(FileStorageService.class);
        textAnalyzer = mock(TextAnalyzer.class);
        controller = new WebSocketController(fileStorageService, textAnalyzer);
    }

    @Test
    void testHandleTextMessage_success() throws Exception {
        MessagePayload payload = new MessagePayload("file.txt", Base64.getEncoder().encodeToString("test content".getBytes()));
        String json = objectMapper.writeValueAsString(payload);

        FileAnalysis analysis = new FileAnalysis("file.txt", 2, 4, 12);
        when(fileStorageService.saveFile(eq("file.txt"), anyString())).thenReturn("path/to/file.txt");
        when(textAnalyzer.analyze(eq("file.txt"), anyString())).thenReturn(analysis);

        TestWebSocketSession session = new TestWebSocketSession();
        controller.handleTextMessage(session, new TextMessage(json));

        String response = session.getLastSentMessage();
        assertTrue(response.contains("\"fileName\":\"file.txt\""));
        assertTrue(response.contains("\"lines\":2"));
        assertTrue(response.contains("\"words\":4"));
        assertTrue(response.contains("\"characters\":12"));
    }

    @Test
    void testHandleTextMessage_invalidJson_shouldReturnError() throws Exception {
        TestWebSocketSession session = new TestWebSocketSession();
        controller.handleTextMessage(session, new TextMessage("{ invalid_json"));

        String response = session.getLastSentMessage();
        assertTrue(response.contains("\"error\":true"));
        assertTrue(response.contains("Failed to parse message"));
    }

    @Test
    void testHandleTextMessage_invalidBase64_shouldReturnError() throws Exception {
        MessagePayload payload = new MessagePayload("file.txt", "not_base64_%%%");
        String json = objectMapper.writeValueAsString(payload);

        when(fileStorageService.saveFile(eq("file.txt"), anyString()))
                .thenThrow(new IllegalArgumentException("Illegal Base64"));

        TestWebSocketSession session = new TestWebSocketSession();
        controller.handleTextMessage(session, new TextMessage(json));

        String response = session.getLastSentMessage();
        assertTrue(response.contains("\"error\":true"));
        assertTrue(response.contains("Illegal Base64"));
    }

    @Test
    void testHandleTextMessage_fileSaveException_shouldReturnError() throws Exception {
        MessagePayload payload = new MessagePayload("file.txt", Base64.getEncoder().encodeToString("test".getBytes()));
        String json = objectMapper.writeValueAsString(payload);

        when(fileStorageService.saveFile(eq("file.txt"), anyString()))
                .thenThrow(new SaveClientFileException("Cannot save file", "test"));

        TestWebSocketSession session = new TestWebSocketSession();
        controller.handleTextMessage(session, new TextMessage(json));

        String response = session.getLastSentMessage();
        assertTrue(response.contains("\"error\":true"));
        assertTrue(response.contains("Cannot save file"));
    }

    @Test
    void testHandleTextMessage_analysisIOException_shouldReturnError() throws Exception {
        MessagePayload payload = new MessagePayload("file.txt", Base64.getEncoder().encodeToString("test".getBytes()));
        String json = objectMapper.writeValueAsString(payload);

        when(fileStorageService.saveFile(eq("file.txt"), anyString())).thenReturn("fake/path.txt");
        when(textAnalyzer.analyze(eq("file.txt"), anyString()))
                .thenThrow(new java.io.IOException("Read error"));

        TestWebSocketSession session = new TestWebSocketSession();
        controller.handleTextMessage(session, new TextMessage(json));

        String response = session.getLastSentMessage();
        assertTrue(response.contains("\"error\":true"));
        assertTrue(response.contains("Read error"));
    }

    @Test
    void testHandleTextMessage_saveAnalysisException_shouldReturnError() throws Exception {
        MessagePayload payload = new MessagePayload("file.txt", Base64.getEncoder().encodeToString("test".getBytes()));
        String json = objectMapper.writeValueAsString(payload);

        FileAnalysis analysis = new FileAnalysis("file.txt", 1, 1, 4);

        when(fileStorageService.saveFile(eq("file.txt"), anyString())).thenReturn("fake/path.txt");
        when(textAnalyzer.analyze(eq("file.txt"), anyString())).thenReturn(analysis);
        doThrow(new SaveAnalysisResultException("Save analysis error", "content"))
                .when(fileStorageService).saveAnalysisResult(eq("file.txt"), eq(analysis));

        TestWebSocketSession session = new TestWebSocketSession();
        controller.handleTextMessage(session, new TextMessage(json));

        String response = session.getLastSentMessage();
        assertTrue(response.contains("\"error\":true"));
        assertTrue(response.contains("Save analysis error"));
    }

    @Test
    void testHandleTextMessage_emptyPayload_shouldReturnError() throws Exception {
        TestWebSocketSession session = new TestWebSocketSession();
        controller.handleTextMessage(session, new TextMessage(""));

        String response = session.getLastSentMessage();
        assertTrue(response.contains("\"error\":true"));
        assertTrue(response.contains("Failed to parse message"));
    }
}

