package com.AidanC.LLMGPT.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class LLMGPTService {

    private final ChatClient chatClient;

    public LLMGPTService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String chat(String message) {
        ChatResponse chatResponse = chatClient.call(new Prompt(message));
        return chatResponse.getResult().getOutput().getContent();
    }

}
