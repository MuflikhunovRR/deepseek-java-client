package us.hubpro.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Represents a request for chat completion in DeepSeek API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    /**
     * The model to be used for chat completion.
     */
    private String model;

    /**
     * A list of messages to be sent in the chat request.
     */
    @NotNull(message = "Messages cannot be null")
    private List<Message> messages;

    /**
     * A value controlling the randomness of the responses.
     * A higher value makes the response more random.
     */
    private double temperature;

    /**
     * Represents a single message in the chat request.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /**
         * The role of the message sender (e.g., "user", "system").
         */
        private String role;

        /**
         * The content of the message.
         */
        private String content;
    }
}
