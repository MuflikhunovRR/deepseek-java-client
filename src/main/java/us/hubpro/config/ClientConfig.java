package us.hubpro.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Configuration class that holds the API key and the base URL for client interactions.
 */
@Getter
@AllArgsConstructor
public class ClientConfig {
    /**
     * The API key used for authenticating with the service.
     */
    private final String apiKey;

    /**
     * The base URL of the service API.
     */
    private final String baseUrl;
}
