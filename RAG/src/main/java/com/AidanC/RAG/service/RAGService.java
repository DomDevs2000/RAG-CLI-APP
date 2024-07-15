package com.AidanC.RAG.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class RAGService {

    private final ChatClient chatClient;
    private final PgVectorStore vectorStore;
    private final Resource ragPromptTemplate;

    @Value("classpath:/prompts/budget-template.st")
    private Resource budgetTemplate;

    @Autowired
    public RAGService(ChatClient chatClient, PgVectorStore vectorStore,
            @Value("classpath:/prompts/rag-prompt-template.st") Resource ragPromptTemplate) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.ragPromptTemplate = ragPromptTemplate;
    }

    public String budget(String message) {
        PromptTemplate promptTemplate = new PromptTemplate(budgetTemplate);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", message);
        promptParameters.put("documents", String.join("\n", findSimilarDocuments(message)));

        return chatClient.call(promptTemplate.create(promptParameters))
                .getResult()
                .getOutput().getContent();
    }

    public String getAnswer(String message) {
        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", message);
        promptParameters.put("documents", String.join("\n", findSimilarDocuments(message)));

        return chatClient.call(promptTemplate.create(promptParameters))
                .getResult()
                .getOutput()
                .getContent();
    }

    private List<String> findSimilarDocuments(String message) {
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(3));
        return similarDocuments.stream().map(Document::getContent).toList();
    }
}

