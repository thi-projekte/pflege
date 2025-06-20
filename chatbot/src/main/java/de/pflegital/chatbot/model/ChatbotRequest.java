package de.pflegital.chatbot.model;

public class ChatbotRequest {
    private String request;
    private String whatsAppNumber;

    public ChatbotRequest() {
    }

    public ChatbotRequest(String request, String whatsAppNumber) {
        this.request = request;
        this.whatsAppNumber = whatsAppNumber;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getWhatsAppNumber() {
        return whatsAppNumber;
    }

    public void setWhatsAppNumber(String whatsAppNumber) {
        this.whatsAppNumber = whatsAppNumber;
    }
}
