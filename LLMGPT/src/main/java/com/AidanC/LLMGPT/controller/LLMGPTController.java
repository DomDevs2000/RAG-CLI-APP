package com.AidanC.LLMGPT.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AidanC.LLMGPT.service.LLMGPTService;
import com.AidanC.LLMGPT.model.LLMGPTResponse;
import groovy.util.logging.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class LLMGPTController {
  private final LLMGPTService llmgptService;

  public LLMGPTController(LLMGPTService llmgptService) {
    this.llmgptService = llmgptService;
  }

  @PostMapping("/chat")
  public ResponseEntity<String> chat(@RequestBody String message) {
    String responseContent = llmgptService.chat(message);
    return ResponseEntity.ok(responseContent);
  }

}
