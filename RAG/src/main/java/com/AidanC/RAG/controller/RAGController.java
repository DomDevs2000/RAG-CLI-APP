package com.AidanC.RAG.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.AidanC.RAG.service.RAGService;
import com.AidanC.RAG.model.RAGResponse;

@RestController
@RequestMapping("/api/v1")
public class RAGController {

    private final RAGService ragService;

    @Autowired
    public RAGController(RAGService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/faq")
    public ResponseEntity<RAGResponse> generateAnswer(
            @RequestParam(value = "message", defaultValue = "summarise a section about project management") String message) {
        String responseContent = ragService.getAnswer(message);
        RAGResponse ragResponse = new RAGResponse(responseContent);
        return ResponseEntity.ok(ragResponse);
    }

    @PostMapping("/test")
    public ResponseEntity<RAGResponse> test(@RequestBody String message) {
        String responseContent = ragService.getAnswer(message);
        RAGResponse faqResponse = new RAGResponse(responseContent);
        return ResponseEntity.ok(faqResponse);

    }
}
