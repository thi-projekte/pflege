package de.pflegital.chatbot.exception;

public class ChatResponseCreationException extends RuntimeException {
    public ChatResponseCreationException(String message) {
        super(message);
    }

    public ChatResponseCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatResponseCreationException(Throwable cause) {
        super(cause);
    }
}
