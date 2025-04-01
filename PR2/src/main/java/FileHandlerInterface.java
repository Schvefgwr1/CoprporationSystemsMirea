import java.io.IOException;

public interface FileHandlerInterface {
    String readContent() throws IOException;
    String getFileName();
}
