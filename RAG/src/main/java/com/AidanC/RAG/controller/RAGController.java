package com.AidanC.RAG.controller;

import com.AidanC.RAG.config.PdfFileReaderConfig;
import com.AidanC.RAG.model.FilePathRequest;
import com.AidanC.RAG.model.RAGResponse;
import com.AidanC.RAG.service.RAGService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  @PostMapping("/chat")
  public ResponseEntity<RAGResponse> chat(@RequestBody String message) {
    String responseContent = ragService.getAnswer(message);
    RAGResponse response = new RAGResponse(responseContent);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/upload")
  public ResponseEntity<String> upload(@RequestBody List<FilePathRequest> requests) {
    try {

      for (FilePathRequest request : requests) {
        String resourcePath = "docs/" + request.getFilePath().trim();
        Resource pdfResource = new ClassPathResource(resourcePath);
        if (!pdfResource.exists()) {
          return ResponseEntity.badRequest().body("File does not exist: " + resourcePath);
        }
        Thread.ofVirtual().start(() -> pdfFileReaderConfig.addResource(pdfResource));
      }
      return ResponseEntity.ok(
          "Files Are Being Processed - Please Be Patient This May Take A While.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body("An error occurred while processing the files: " + e.getMessage());
    }
  }
}

