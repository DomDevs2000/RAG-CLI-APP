package com.AidanC.LLMGPT.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class OllamaLLMService {

  private final OllamaChatModel chatClient;

  public OllamaLLMService(OllamaChatModel chatClient) {
    this.chatClient = chatClient;
  }

  public String chat(String message) {
    ChatResponse chatResponse = chatClient.call(new Prompt(message));
    return chatResponse.getResult().getOutput().getContent();
  }

}
