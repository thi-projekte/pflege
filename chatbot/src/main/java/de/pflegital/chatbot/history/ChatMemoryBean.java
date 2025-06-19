package de.pflegital.chatbot.history;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ChatMemoryBean implements ChatMemoryProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ChatMemoryBean.class);

    Map<Object, ChatMemory> memories = new ConcurrentHashMap<>();

    @Override
    public ChatMemory get(Object memoryId) {
        LOG.info("Abruf/Anlage ChatMemory für memoryId: {}", memoryId);
        return memories.computeIfAbsent(memoryId,
                id -> {
                    LOG.info("Neues ChatMemory wird angelegt für memoryId: {}", id);
                    return MessageWindowChatMemory.builder()
                            .maxMessages(120)
                            .id(memoryId)
                            .build();
                });
    }

    @PreDestroy
    public void close() {
        LOG.info("Alle ChatMemories werden gelöscht.");
        memories.clear();
    }
}
