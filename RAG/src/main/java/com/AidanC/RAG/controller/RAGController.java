package com.AidanC.RAG.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.AidanC.RAG.config.PdfFileReaderConfig;
import com.AidanC.RAG.model.FilePathRequest;
import com.AidanC.RAG.model.RAGResponse;
import com.AidanC.RAG.service.RAGService;

@RestController
@RequestMapping("/api/v1")
public class RAGController {

    private final RAGService ragService;
    private final PdfFileReaderConfig pdfFileReaderConfig;

    @Autowired
    public RAGController(RAGService ragService, PdfFileReaderConfig pdfFileReaderConfig) {
        this.ragService = ragService;
        this.pdfFileReaderConfig = pdfFileReaderConfig;
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

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestBody List<FilePathRequest> requests) {
        try {
            for (FilePathRequest request : requests) {
                String resourcePath = "docs/" + request.getFilePath().trim();
                Resource pdfResource = new ClassPathResource(resourcePath);
                if (!pdfResource.exists()) {
                    return ResponseEntity.badRequest().body("File does not exist: " +
                            resourcePath);
                }
                pdfFileReaderConfig.addResource(pdfResource);
            }
            return ResponseEntity.ok("File processing started...Please be patient this may take a while.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while processing the files: " + e.getMessage());
        }
    }
}
