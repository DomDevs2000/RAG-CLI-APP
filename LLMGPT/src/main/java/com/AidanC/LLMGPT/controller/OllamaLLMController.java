package com.AidanC.LLMGPT.controller;


 import org.springframework.ai.chat.client.ChatClient;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RestController;

 import com.AidanC.LLMGPT.service.LLMGPTService;
 import com.AidanC.LLMGPT.service.OllamaLLMService;
 import com.AidanC.LLMGPT.model.LLMGPTResponse;
 import groovy.util.logging.Slf4j;

 import org.springframework.ai.ollama.OllamaChatModel;
 @RestController
 @RequestMapping("/ollama")
 @Slf4j
 public class OllamaLLMController {
 private final OllamaLLMService ollamaLLMService;

 private final OllamaChatModel ollamaChatModel;

 public OllamaLLMController(OllamaLLMService
 ollamaLLMService, OllamaChatModel ollamaChatModel) {
 this.ollamaLLMService = ollamaLLMService;
 this.ollamaChatModel = ollamaChatModel;
 }

 @PostMapping("/chat")
 public ResponseEntity<LLMGPTResponse> test(@RequestBody String message) {
 String responseContent = ollamaLLMService.chat(message);
 LLMGPTResponse chatResponse = new LLMGPTResponse();
 chatResponse.setContent(responseContent);
 return ResponseEntity.ok(chatResponse);
 }

 }
