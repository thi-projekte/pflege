package de.pflegital.chatbot;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class SessionStore {
    private final Map<String, FormData> sessions = new HashMap<>();
    public FormData getFormData(String sessionId){
        return sessions.get(sessionId);
    }
    public void setFormData(String sessionId, FormData formData){
sessions.put(sessionId,formData);
    }
}
