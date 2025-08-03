package com.AidanC.RAG.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class RAGConfig {

  @Bean
  public ChatClient chatClient(OllamaChatModel chatModel) {
    return ChatClient.builder(chatModel).build();
  }

  @Bean
  public PgVectorStore vectorStore(JdbcTemplate jdbcTemplate,
      OllamaEmbeddingModel embeddingModel) {
    PgVectorStore vectorStore = new PgVectorStore(jdbcTemplate, embeddingModel);
    return vectorStore;
  }
}

