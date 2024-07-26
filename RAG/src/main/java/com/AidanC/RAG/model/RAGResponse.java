package com.AidanC.RAG.model;

public class RAGResponse {
  private String content;

  public RAGResponse(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
