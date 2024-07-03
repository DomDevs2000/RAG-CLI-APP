package com.AidanC.CLI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import com.AidanC.CLI.AppConfig;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;


@Command
public class ApiCommand {

    private final WebClient webClient;

    public ApiCommand(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    private static final String API_URL = "http://localhost:8080/api/v1/faq"; // predefined URL

    private static final String TEST_URL = "http://localhost:8080/api/v1/test";


    @Command(command = "api-test")
    public String test(String message) {
        return hitApi(message);
    }

//    @Command(command="test")
//    public String testApi(String message){
//        String response = restTemplate.postForObject(message, TEST_URL, String.class);
//        return "Response: " + response;
//    }
    @Command(command = "example")
    public String example(@RequestBody String message) {
        return hitApi(message);
    }

    @ShellMethod("test method")
    public String hitApi(String message) {
        ApiResponse response = webClient.post()
                .uri(TEST_URL).
        contentType(MediaType.APPLICATION_JSON)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block(); // blocking call for simplicity in CLI app
        return response != null ? response.getContent() : "No response received";
    }
}


