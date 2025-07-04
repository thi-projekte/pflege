package de.pflegital.chatbot.exception;

public class BpmnProcessException extends RuntimeException {
    public BpmnProcessException(String message) {
        super(message);
    }

    public BpmnProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BpmnProcessException(Throwable cause) {
        super(cause);
    }
}
