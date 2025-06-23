package de.pflegital.chatbot;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class SessionStore {
    private final Map<String, FormData> sessions = new HashMap<>();

    public FormData getFormData(String memoryId) {
        return sessions.get(memoryId);
    }

    public void setFormData(String memoryId, FormData formData) {
        sessions.put(memoryId, formData);
    }

    public void removeFormData(String memoryId) {
        sessions.remove(memoryId);
    }
}
