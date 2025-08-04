package com.AidanC.CLI;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@ShellComponent
public class ApiCommand {

    private final WebClient webClient;
    private final OutputFormatter formatter;

    @Autowired
    public ApiCommand(WebClient.Builder webClientBuilder, OutputFormatter formatter) {
        this.webClient = webClientBuilder.build();
        this.formatter = formatter;
    }

    @ShellMethod("Chat with your documents")
    public String chat(@ShellOption String message) {
        try {
            ApiResponse response = webClient.post()
                    .uri("http://localhost:8080/api/v1/chat").contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(message)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();
            
            String content = response != null ? response.getContent() : "No response received";
            return formatter.formatResponse(content);
        } catch (Exception e) {
            return formatter.formatError("Failed to get response: " + e.getMessage());
        }
    }

    @ShellMethod("Upload PDF documents to the knowledge base")
    public String upload(@ShellOption List<String> filePaths) {
        try {
            List<FilePathRequest> requests = filePaths.stream()
                    .map(FilePathRequest::new)
                    .collect(Collectors.toList());

            String response = webClient.post()
                    .uri("http://localhost:8080/api/v1/upload")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requests)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String message = response != null ? response : "No response received from the server.";
            return formatter.formatSuccess(message);
        } catch (Exception e) {
            return formatter.formatError("Failed to upload files: " + e.getMessage());
        }
    }

    @ShellMethod(value = "Refresh database by clearing all embeddings", key = "refreshdb")
    public String refreshDb() {
        try {
            String response = webClient.delete()
                    .uri("http://localhost:8080/api/v1/refreshdb")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String message = response != null ? response : "No response received from the server.";
            return formatter.formatSuccess(message);
        } catch (Exception e) {
            return formatter.formatError("Failed to refresh database: " + e.getMessage());
        }
    }

}
