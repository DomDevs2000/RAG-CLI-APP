package com.AidanC.LLMGPT.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class LLMGPTService {
    private final OpenAiChatModel chatClient;

    public LLMGPTService(OpenAiChatModel chatClient) {
        this.chatClient = chatClient;
    }

    public String chat(String message) {
        var prompt = new Prompt(message);
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }

}
