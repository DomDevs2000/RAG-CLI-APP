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

    @GetMapping("/opening-balance")
    public ResponseEntity<RAGResponse> generateAnswer(
            @RequestParam(value = "message", defaultValue = "What was the opening balance each month of 2023 ") String message) {
        String responseContent = ragService.getAnswer(message);
        RAGResponse OBResponse = new RAGResponse(responseContent);
        return ResponseEntity.ok(OBResponse);
    }

    @PostMapping("/chat")
    public ResponseEntity<RAGResponse> test(@RequestBody String message) {
        String responseContent = ragService.getAnswer(message);
        RAGResponse chatResponse = new RAGResponse(responseContent);
        return ResponseEntity.ok(chatResponse);

    }

    @GetMapping("/budget")
    public ResponseEntity<RAGResponse> budget(
            @RequestParam(value = "message", defaultValue = "Based on the monthly outgoings, what cost can be cut down to help save money") String message) {
        String responseContent = ragService.budget(message);
        RAGResponse budgetResponse = new RAGResponse(responseContent);
        return ResponseEntity.ok(budgetResponse);
    }
}
