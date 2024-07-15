package com.AidanC.CLI;

import org.springframework.http.MediaType;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.WebClient;

@Command
public class ApiCommand {

    private final WebClient webClient;

    public ApiCommand(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    private static final String API_URL = "http://msc-rag-api-28879415.eu-west-2.elb.amazonaws.com/api/v1/budget";
    private static final String BUDGET_URL = "http://msc-rag-api-28879415.eu-west-2.elb.amazonaws.com/api/v1/budget";
    private static final String TEST_URL = "http://35.177.40.177:8080/api/v1/test";

    @Command(command = "faq")
    public String test() {
        return getResponse();
    }

    @Command(command = "budget")
    public String budget() {
        ApiResponse response = webClient.get().uri(BUDGET_URL).retrieve()
                .bodyToMono(ApiResponse.class)
                .block();
        return response != null ? response.getContent() : "No response received";
    }

    @Command(command = "test")
    public String example(String message) {
        ApiResponse response = webClient.post()
                .uri(TEST_URL).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block();
        return response != null ? response.getContent() : "No response received";
    }

    @ShellMethod("test-method")
    public String postQuery(String message) {
        ApiResponse response = webClient.post()
                .uri(TEST_URL).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block();
        return response != null ? response.getContent() : "No response received";

    }

    @ShellMethod("get-response")
    public String getResponse() {
        ApiResponse response = webClient.get()
                .uri(API_URL).retrieve()
                .bodyToMono(ApiResponse.class)
                .block();
        return response != null ? response.getContent() : "No response received";
    }
}
