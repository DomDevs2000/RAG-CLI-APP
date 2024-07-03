package com.AidanC.RAG.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
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

    @Autowired
    public RAGService(ChatClient chatClient, PgVectorStore vectorStore,
                      @Value("classpath:/prompts/rag-prompt-template.st") Resource ragPromptTemplate) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.ragPromptTemplate = ragPromptTemplate;
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

// Alternate simiilarity search implementation (harder to read - no top k result -- OLD
//        List<Document> documents = this.vectorStore.similaritySearch(message);
//        String collect = documents.stream().map(Document::getContent)
//                .collect(Collectors.joining(System.lineSeparator()));
//
//        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
//        Map<String, Object> promptParameters = new HashMap<>();
//        promptParameters.put("input", message);
//        promptParameters.put("documents", String.join("\n", collect));
//
//        Prompt prompt = promptTemplate.create(promptParameters);
//        return chatClient.call(prompt).getResults().stream().map(generation -> {
//            return generation.getOutput().getContent();
//        }).collect(Collectors.joining("/n"));
//    }
//}
