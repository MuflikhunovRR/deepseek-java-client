package us.hubpro;

import us.hubpro.client.DeepSeekClient;
import us.hubpro.exception.DeepSeekApiException;
import us.hubpro.model.request.ChatRequest;
import us.hubpro.model.response.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Example {
    private static final Logger logger = LoggerFactory.getLogger(Example.class);

    private static final String MODEL = "deepseek-chat";
    private static final String APIKEY = "YOUR_API_KEY";
    private static final double TEMPERATURE = 0.7;

    public static void main(String[] args) {
        logger.info("Starting DeepSeek client example");

        DeepSeekClient client = DeepSeekClient.builder()
                .apiKey(APIKEY)
                .build();

        try {
            ChatRequest request = new ChatRequest();
            request.setModel(MODEL);
            request.setMessages(List.of(new ChatRequest.Message("user", "Hello, how are you?")));
            request.setTemperature(TEMPERATURE);

            logger.debug("Sending request: {}", request);
            ChatResponse response = client.chatCompletion(request);

            if (response != null && !response.getChoices().isEmpty()) {
                logger.info("Response: {}", response.getChoices().get(0).getMessage().getContent());
            } else {
                logger.error("No valid response received");
            }
        } catch (DeepSeekApiException e) {
            logger.error("API request failed: {}", e.getMessage(), e);
        } finally {
            try {
                client.close();
                logger.info("DeepSeek client closed successfully");
            } catch (IOException e) {
                logger.error("Failed to close client: {}", e.getMessage(), e);
            }
        }

        logger.info("DeepSeek client example finished");
    }
}
