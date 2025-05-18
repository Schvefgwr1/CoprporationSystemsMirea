package test.pr3_server.controllers_tests;

import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.*;

import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public class TestWebSocketSession implements WebSocketSession {

    private String lastSent;

    @Override
    public void sendMessage(WebSocketMessage<?> message) {
        this.lastSent = message.getPayload().toString();
    }

    public String getLastSentMessage() {
        return lastSent;
    }

    // Остальные методы заглушки (если понадобятся — можно замокать)

    @Override public String getId() { return "test-session"; }
    @Override public URI getUri() { return null; }
    @Override public Map<String, Object> getAttributes() { return null; }
    @Override public Principal getPrincipal() { return null; }
    @Override public InetSocketAddress getLocalAddress() { return null; }
    @Override public InetSocketAddress getRemoteAddress() { return null; }
    @Override public String getAcceptedProtocol() { return null; }

    @Override
    public void setTextMessageSizeLimit(int messageSizeLimit) {

    }

    @Override
    public int getTextMessageSizeLimit() {
        return 0;
    }

    @Override
    public void setBinaryMessageSizeLimit(int messageSizeLimit) {

    }

    @Override
    public int getBinaryMessageSizeLimit() {
        return 0;
    }

    @Override
    public List<WebSocketExtension> getExtensions() {
        return List.of();
    }

    @Override public void close() {}
    @Override public void close(CloseStatus closeStatus) {}
    @Override public boolean isOpen() { return true; }
    @Override
    public HttpHeaders getHandshakeHeaders() {
        return new HttpHeaders(); // можно вернуть пустой или замоканный объект
    }

}
