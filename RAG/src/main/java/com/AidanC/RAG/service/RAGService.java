package com.AidanC.RAG.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class RAGService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final Resource ragPromptTemplate;

    @Autowired
    public RAGService(ChatClient chatClient, VectorStore vectorStore,
            @Value("classpath:/prompts/rag-prompt-template.st") Resource ragPromptTemplate) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.ragPromptTemplate = ragPromptTemplate;
    }

    public String getAnswer(String message) {
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(2));
        List<String> contentList = similarDocuments.stream().map(Document::getContent).toList();
        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", message);
        promptParameters.put("documents", String.join("\n", contentList));
        Prompt prompt = promptTemplate.create(promptParameters);

        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
