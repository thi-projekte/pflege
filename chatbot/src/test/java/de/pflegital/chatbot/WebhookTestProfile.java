package de.pflegital.chatbot;

import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.Map;
import java.util.HashMap;

public class WebhookTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        Map<String, String> config = new HashMap<>();
        // Mock the OpenAI config
        config.put("quarkus.langchain4j.openai.api-key", "sk-test-key-not-real");
        // Add your webhook token
        config.put("whatsapp.verify.token", "winfprojekt");
        return config;
    }
}