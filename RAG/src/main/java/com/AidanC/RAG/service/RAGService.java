package com.AidanC.RAG.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class RAGService {
  @Autowired
  private final OpenAiChatModel chatClient;
  private final PgVectorStore vectorStore;
  private final Resource ragPromptTemplate;

  @Autowired
  public RAGService(
      OpenAiChatModel chatClient,
      PgVectorStore vectorStore,
      @Value("classpath:/prompts/rag-prompt-template.st") Resource ragPromptTemplate) {
    this.chatClient = chatClient;
    this.vectorStore = vectorStore;
    this.ragPromptTemplate = ragPromptTemplate;
  }

  public ChatResponse getAnswer(String message) {
    var prompt = createPrompt(message);
    return chatClient.call(prompt);
  }

  public ChatResponse getMetadata(String message) {
    var prompt = createPrompt(message);

    return chatClient.call(prompt);
  }

  private List<String> findSimilarDocuments(String message) {
    List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(5));
    return similarDocuments.stream().map(Document::getContent).toList();
  }

  private Prompt createPrompt(String message) {
    PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
    Map<String, Object> promptParameters = new HashMap<>();
    promptParameters.put("input", message);
    promptParameters.put("documents", String.join("\n", findSimilarDocuments(message)));
    return promptTemplate.create(promptParameters);
  }
}
