package com.AidanC.RAG;

import com.AidanC.RAG.controller.RAGController;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.core.io.Resource;
import org.springframework.shell.command.annotation.Command;

import org.springframework.ai.vectorstore.PgVectorStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Command
public class SpringCommandRun {

    private ChatClient chatClient;
    private PgVectorStore vectorStore;

    private RAGController ragController;

    @Value("classpath:/prompts/rag-prompt-template.st")
    Resource ragPromptTemplate;


    public SpringCommandRun(ChatClient chatClient, PgVectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @Command(command = "q")
    public String question(String message) {

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



