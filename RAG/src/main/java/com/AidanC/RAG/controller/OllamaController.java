package com.AidanC.RAG.controller;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AidanC.RAG.service.OllamaRAGService;

@RestController
@RequestMapping("/ollama/v1")
public class OllamaController {

  private final OllamaRAGService ragService;

  public OllamaController(OllamaRAGService ragService) {
    this.ragService = ragService;
  }

  @PostMapping("/chat")
  public ResponseEntity<ChatResponse> generate(
      @RequestBody String message) {

    return ResponseEntity.ok(ragService.getAnswer(message));
  }

}
