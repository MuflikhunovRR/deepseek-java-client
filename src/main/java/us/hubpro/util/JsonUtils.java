package us.hubpro.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Converts an object to a JSON string.
     * @param object The object to be converted to JSON.
     * @return The JSON string representation of the object.
     */
    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("Failed to serialize object to JSON: {}", e.getMessage(), e);
            throw new JsonSerializationException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * Converts a JSON string to an object of the specified type.
     * @param json The JSON string.
     * @param valueType The class of the object to be deserialized.
     * @param <T> The type of the object.
     * @return The deserialized object.
     */
    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return mapper.readValue(json, valueType);
        } catch (Exception e) {
            logger.error("Failed to deserialize JSON: {}", e.getMessage(), e);
            throw new JsonDeserializationException("Failed to deserialize JSON", e);
        }
    }

    // Custom exceptions for serialization and deserialization errors
    public static class JsonSerializationException extends RuntimeException {
        public JsonSerializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class JsonDeserializationException extends RuntimeException {
        public JsonDeserializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
