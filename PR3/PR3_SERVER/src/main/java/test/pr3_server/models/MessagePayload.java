package test.pr3_server.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagePayload {
    private String fileName;
    private String content; // Base64-encoded
}
