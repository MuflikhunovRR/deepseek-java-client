# DeepSeek Java Client

This is a Java client library for interacting with the DeepSeek API, providing functionality for chat completions and more. It demonstrates how to easily integrate DeepSeek services into Java applications.

## Features

- Chat completion with configurable parameters like model, messages, and temperature.
- Error handling with detailed logging and exception management.

## Requirements

- Java 17 or higher
- Maven or Gradle (for managing dependencies)

## Code Example

The following code demonstrates how to use the `DeepSeekClient` to send a chat request and handle the response:

```java
import us.hubpro.DeepSeekClient;
import us.hubpro.model.request.ChatRequest;
import us.hubpro.model.response.ChatResponse;

public class Example {
    public static void main(String[] args) {
        DeepSeekClient client = new DeepSeekClient(System.getenv("DEEPSEEK_API_KEY"));

        try {
            ChatRequest request = new ChatRequest("deepseek-chat", List.of(new ChatRequest.Message("user", "Hello!")));
            ChatResponse response = client.chatCompletion(request);
            System.out.println("Response: " + response.getChoices().get(0).getMessage().getContent());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            client.close();
        }
    }
}



License
This project is for educational purposes only. All rights to the DeepSeek API belong to the respective parties.

Acknowledgments
DeepSeek API for providing the powerful language models.
SLF4J and Logback for excellent logging.
Maven/Gradle for dependency management and project builds.

## Contact
Feel free to connect with me on [LinkedIn](https://www.linkedin.com/in/your-profile) for any inquiries or collaborations.
