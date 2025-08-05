package com.AidanC.RAG;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.ai.model.Content;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.AidanC.RAG.config.PdfFileReaderConfig;

@SpringBootTest
@Testcontainers
public class RAGEvaluationTests {

    @Container
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16")
            .withDatabaseName("rag_eval_test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("test-db-init.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private static final Logger logger = LoggerFactory.getLogger(RAGEvaluationTests.class);

    @Autowired
    private OllamaChatModel ollamaChatModel;

    @Autowired
    private PgVectorStore vectorStore;

    @Autowired
    private PdfFileReaderConfig pdfFileReaderConfig;

    @Value("classpath:/docs/Apple_AnnualReport_2023.pdf")
    private Resource pdfResource;

    private static boolean documentLoaded = false;

    @BeforeEach
    void setupTestData() throws InterruptedException {
        if (!documentLoaded) {
            logger.info("LOADING EVALUATION TEST DATA ==================================");
            logger.info("Using isolated test database: {}", postgres.getJdbcUrl());

            if (pdfResource.exists()) {
                logger.info("Loading test document: {}", pdfResource.getFilename());
                pdfFileReaderConfig.addResource(pdfResource);
                logger.info("Test document loaded successfully");
                documentLoaded = true;
            } else {
                logger.warn("Test document not found: {}", pdfResource.getFilename());
            }
        }
    }

    @Test
    void testEvaluationEconomicFactors() throws InterruptedException {
        String query = "Summarise the economic factors that affected apple";
        ChatResponse response = ChatClient.builder(ollamaChatModel)
                .build().prompt()
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.query(query).withTopK(3)))
                .user(query)
                .call()
                .chatResponse();

        var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(ollamaChatModel));

        @SuppressWarnings("unchecked")
        List<Content> retrievedDocuments = (List<Content>) response.getMetadata()
                .get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);
        EvaluationRequest evaluationRequest = new EvaluationRequest(query, retrievedDocuments, response);

        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");
    }

    @Test
    void testEvaluationFiscalPeriod() throws InterruptedException {
        String query = "How long is Apple's fiscal period";

        ChatResponse response = ChatClient.builder(ollamaChatModel)
                .build().prompt()
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.query(query).withTopK(3)))
                .user(query)
                .call()
                .chatResponse();

        var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(ollamaChatModel));

        @SuppressWarnings("unchecked")
        List<Content> retrievedDocuments = (List<Content>) response.getMetadata()
                .get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);
        EvaluationRequest evaluationRequest = new EvaluationRequest(query, retrievedDocuments, response);

        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");
    }

    @Test
    void testEvaluationRevenue() throws InterruptedException {
        String query = "What was Apple's total revenue in 2023";

        ChatResponse response = ChatClient.builder(ollamaChatModel)
                .build().prompt()
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.query(query).withTopK(3)))
                .user(query)
                .call()
                .chatResponse();

        var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(ollamaChatModel));

        @SuppressWarnings("unchecked")
        List<Content> retrievedDocuments = (List<Content>) response.getMetadata()
                .get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);
        EvaluationRequest evaluationRequest = new EvaluationRequest(query, retrievedDocuments, response);

        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");
    }

    @Test
    void testEvaluationIrrelevantQuery() throws InterruptedException {
        String query = "Why is the sky blue";

        ChatResponse response = ChatClient.builder(ollamaChatModel)
                .build().prompt()
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.query(query).withTopK(3)))
                .user(query)
                .call()
                .chatResponse();

        var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(ollamaChatModel));

        @SuppressWarnings("unchecked")
        List<Content> retrievedDocuments = (List<Content>) response.getMetadata()
                .get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);
        EvaluationRequest evaluationRequest = new EvaluationRequest(query, retrievedDocuments, response);

        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        assertTrue(evaluationResponse.getScore() == 0.0, "Response should not be relevant to Apple documents");
    }
}
