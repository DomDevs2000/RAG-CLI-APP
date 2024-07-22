package com.AidanC.CLI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@ShellComponent
public class ApiCommand {

    private final WebClient webClient;

    @Autowired
    public ApiCommand(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    // private static final String HOSTED_URL =
    // "http://msc-rag-api-28879415.eu-west-2.elb.amazonaws.com/api/v1/budget";
    private static final String LOCAL_URL = "http://localhost:8080/api/v1/chat";

    private static final String BUDGET_URL = "http://localhost:8080/api/v1/budget";

    @ShellMethod("Chat Command")
    public String chat(@ShellOption String message) {
        ApiResponse response = webClient.post()
                .uri(LOCAL_URL).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block();
        return response != null ? response.getContent() : "No response received";
    }

    @ShellMethod("Budget Command")
    public String budget() {
        ApiResponse response = webClient.get()
                .uri(BUDGET_URL)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block();
        return response != null ? response.getContent() : "No response received";
    }

}
