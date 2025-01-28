package us.hubpro.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom exception class for handling errors related to DeepSeek API interactions.
 */
public class DeepSeekApiException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekApiException.class);

    /**
     * Constructor for exception with message.
     * @param message Error message
     */
    public DeepSeekApiException(String message) {
        super(message);
        logger.error("DeepSeek API Exception: {}", message);
    }

    /**
     * Constructor for exception with message and cause.
     * @param message Error message
     * @param cause The underlying cause of the exception
     */
    public DeepSeekApiException(String message, Throwable cause) {
        super(message, cause);
        logger.error("DeepSeek API Exception: {}. Cause: {}", message, cause != null ? cause.getMessage() : "N/A", cause);
    }
}
