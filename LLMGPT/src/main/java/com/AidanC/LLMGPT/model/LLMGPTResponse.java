package com.AidanC.LLMGPT.model;

public class LLMGPTResponse {
    private String content;

    public LLMGPTResponse(String content){
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
