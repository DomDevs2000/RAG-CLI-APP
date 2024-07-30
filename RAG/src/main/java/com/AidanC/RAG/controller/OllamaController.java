package com.AidanC.RAG.controller;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AidanC.RAG.service.RAGService;

@RestController
@RequestMapping("/v1/ollama")
public class OllamaController {

  private final RAGService ragService;

  public OllamaController(RAGService ragService) {
    this.ragService = ragService;
  }

  @PostMapping("/chat")
  public ResponseEntity<ChatResponse> generate(
      @RequestBody String message) {

    return ResponseEntity.ok(ragService.getAnswer(message));
  }

}
