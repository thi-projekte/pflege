package de.pflegital.chatbot.exception;

public class WhatsAppApiException extends RuntimeException {
    public WhatsAppApiException(String message) {
        super(message);
    }

    public WhatsAppApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public WhatsAppApiException(Throwable cause) {
        super(cause);
    }
}
