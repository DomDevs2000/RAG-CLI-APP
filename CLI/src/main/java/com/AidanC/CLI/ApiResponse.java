package com.AidanC.CLI;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse {

    @JsonProperty("content")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
