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
        config.put("whatsapp.api.token", "dummy-test-token");
        config.put("whatsapp.phone.number.id", "dummy-test-phone-number-id");
        config.put("whatsapp.api.version", "v22.0");
        return config;
    }
}
