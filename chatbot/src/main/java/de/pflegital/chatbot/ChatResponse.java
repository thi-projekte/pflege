package de.pflegital.chatbot;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatResponse {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("formData")
    private FormData formData;

    public ChatResponse() {
    }

    public ChatResponse(String sessionId, FormData formData) {
        this.sessionId = sessionId;
        this.message = formData.getChatbotMessage();
        this.formData = formData;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FormData getFormData() {
        return formData;
    }

    public void setFormData(FormData formData) {
        this.formData = formData;
    }
}
