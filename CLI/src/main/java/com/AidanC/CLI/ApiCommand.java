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

    @Autowired
    public ApiCommand(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    private static final String CHAT_URL = "http://13.42.13.199:8080/api/v1/chat";
    private static final String UPLOAD_URL = "http://13.42.13.199:8080/api/v1/upload";

    @ShellMethod("Chat Command")
    public String chat(@ShellOption String message) {
        ApiResponse response = webClient.post()
                .uri(CHAT_URL).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block();
        return response != null ? response.getContent() : "No response received";
    }

    @ShellMethod("Upload Command")
    public String upload(@ShellOption List<String> filePaths) {
        try {
            List<FilePathRequest> requests = filePaths.stream()
                    .map(FilePathRequest::new)
                    .collect(Collectors.toList());

            String response = webClient.post()
                    .uri(UPLOAD_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requests)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response != null ? response : "No response received from the server.";
        } catch (Exception e) {
            return "An error occurred while uploading the files: " + e.getMessage();
        }
    }

}
