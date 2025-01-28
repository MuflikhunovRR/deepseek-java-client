package us.hubpro.client;

import us.hubpro.config.ClientConfig;
import us.hubpro.exception.DeepSeekApiException;
import us.hubpro.model.request.ChatRequest;
import us.hubpro.model.response.ChatResponse;
import us.hubpro.util.JsonUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeepSeekClient implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekClient.class);

    private final ClientConfig config;
    private final CloseableHttpClient httpClient;

    // Private constructor for use via the Builder
    private DeepSeekClient(ClientConfig config) {
        this.config = config;
        this.httpClient = createSecureHttpClient();
    }

    /**
     * Creates and returns a new instance of DeepSeekClientBuilder.
     * @return DeepSeekClientBuilder
     */
    public static DeepSeekClientBuilder builder() {
        return new DeepSeekClientBuilder();
    }

    /**
     * Creates a secure HTTP client with default settings.
     * @return CloseableHttpClient
     */
    private CloseableHttpClient createSecureHttpClient() {
        return HttpClients.custom()
                .useSystemProperties() // Uses system security settings
                .evictExpiredConnections() // Automatically closes expired connections
                .setConnectionManagerShared(false) // Not using shared connection pool
                .build();
    }

    /**
     * Makes a request to the DeepSeek API for chat completion.
     * @param request ChatRequest object
     * @return ChatResponse object
     * @throws DeepSeekApiException If there is an error with the API request
     */
    public ChatResponse chatCompletion(ChatRequest request) throws DeepSeekApiException {
        final HttpPost httpPost = new HttpPost(config.getBaseUrl() + "/v1/chat/completions");
        setRequestHeaders(httpPost);
        setRequestBody(httpPost, request);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return processResponse(response);
        } catch (IOException e) {
            throw new DeepSeekApiException("API request failed", e);
        }
    }

    /**
     * Sets the headers for the HTTP request.
     * @param httpPost HTTP request
     */
    private void setRequestHeaders(HttpPost httpPost) {
        httpPost.setHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setHeader("Authorization", "Bearer " + config.getApiKey());
    }

    /**
     * Sets the body for the HTTP request.
     * @param httpPost HTTP request
     * @param request Body of the request (ChatRequest)
     */
    private void setRequestBody(HttpPost httpPost, ChatRequest request) {
        final String jsonRequest = JsonUtils.toJson(request);
        httpPost.setEntity(new StringEntity(jsonRequest, ContentType.APPLICATION_JSON));
    }

    /**
     * Processes the HTTP response.
     * @param response HTTP response
     * @return ChatResponse object
     * @throws IOException If there is an error reading the response
     * @throws DeepSeekApiException If the response status is not successful
     */
    private ChatResponse processResponse(CloseableHttpResponse response) throws DeepSeekApiException {
        final int statusCode = response.getCode();
        final String responseBody;

        try {
            responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            logger.debug("Response body: {}", responseBody);
        } catch (IOException | org.apache.hc.core5.http.ParseException e) {
            logger.error("Failed to parse API response: {}", e.getMessage(), e);
            throw new DeepSeekApiException("Failed to parse API response", e);
        }

        // Handle specific errors
        if (statusCode == 402) {
            logger.error("Insufficient balance. Please top up your account.");
            throw new DeepSeekApiException("Insufficient balance. Please top up your account.");
        } else if (statusCode < 200 || statusCode >= 300) {
            logger.error("API returned error. Status code: {}, Response: {}", statusCode, responseBody);
            throw new DeepSeekApiException("API Error [" + statusCode + "]: " + responseBody);
        }

        try {
            return JsonUtils.fromJson(responseBody, ChatResponse.class);
        } catch (RuntimeException e) {
            logger.error("Failed to deserialize API response: {}", e.getMessage(), e);
            throw new DeepSeekApiException("Failed to deserialize API response", e);
        }
    }

    /**
     * Closes the HTTP client and releases resources.
     * @throws IOException If there is an error closing the client
     */
    @Override
    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    /**
     * Internal Builder class for creating instances of DeepSeekClient.
     */
    public static class DeepSeekClientBuilder {
        private static final Logger logger = LoggerFactory.getLogger(DeepSeekClientBuilder.class);
        private String apiKey;
        private String baseUrl = "https://api.deepseek.com";

        /**
         * Sets the API key.
         * @param apiKey API key
         * @return DeepSeekClientBuilder
         */
        public DeepSeekClientBuilder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Sets the base URL for the API.
         * @param baseUrl Base URL
         * @return DeepSeekClientBuilder
         */
        public DeepSeekClientBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Builds an instance of DeepSeekClient.
         * @return DeepSeekClient
         * @throws IllegalArgumentException If the API key or base URL is missing
         */
        public DeepSeekClient build() {
            validateConfiguration();
            logger.info("Creating DeepSeekClient with base URL: {}", baseUrl);
            return new DeepSeekClient(new ClientConfig(apiKey, baseUrl));
        }

        /**
         * Validates the configuration.
         * @throws IllegalArgumentException If the configuration is invalid
         */
        private void validateConfiguration() {
            if (apiKey == null || apiKey.isBlank()) {
                logger.error("API key is missing or empty");
                throw new IllegalArgumentException("API key must be provided");
            }
            if (baseUrl == null || baseUrl.isBlank()) {
                logger.error("Base URL is missing or empty");
                throw new IllegalArgumentException("Base URL cannot be empty");
            }
        }
    }
}
