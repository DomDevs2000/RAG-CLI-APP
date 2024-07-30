package com.AidanC.RAG.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class RAGConfig {

  // @Bean
  // public ChatClient chatClient(ChatClient.Builder builder) {
  // return builder.defaultSystem("Test").build();
  // }

  @Bean
  public PgVectorStore vectorStore(JdbcTemplate jdbcTemplate,
      OpenAiEmbeddingModel embeddingModel) {
    PgVectorStore vectorStore = new PgVectorStore(jdbcTemplate, embeddingModel);
    return vectorStore;
  }
}
