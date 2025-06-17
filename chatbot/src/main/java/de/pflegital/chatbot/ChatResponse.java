package de.pflegital.chatbot;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatResponse {

    @JsonProperty("memoryId")
    private String memoryId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("formData")
    private FormData formData;

    public ChatResponse() {
    }

    public ChatResponse(String memoryId, FormData formData) {
        this.memoryId = memoryId;
        this.message = formData.getChatbotMessage();
        this.formData = formData;
    }

    public String getMemoryId() {
        return memoryId;
    }

    public void setMemoryId(String memoryId) {
        this.memoryId = memoryId;
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
