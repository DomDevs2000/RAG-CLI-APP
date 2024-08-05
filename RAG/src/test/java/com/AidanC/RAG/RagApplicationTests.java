package com.AidanC.RAG;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
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
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import com.AidanC.RAG.service.OllamaRAGService;
import com.AidanC.RAG.service.RAGService;

import groovy.util.logging.Slf4j;

@SpringBootTest
@Slf4j
public class RagApplicationTests {
  private static final Logger logger = LoggerFactory.getLogger(RagApplicationTests.class);
  @Autowired
  private OpenAiChatModel openAiChatModel;

  @Autowired
  private OllamaChatModel ollamaChatModel;

  @Autowired
  private PgVectorStore vectorStore;

  @Autowired
  private OllamaRAGService ollamaRagService;

  @Autowired
  private RAGService ragService;
  @Value("classpath:/docs/Apple_AnnualReport_2023.pdf")
  private Resource pdfResource;

  @BeforeEach
  void beforeTest() throws InterruptedException {
    logger.info("TEST STARTED ==================================");
  }

  @AfterEach
  void afterTest() throws InterruptedException {
    logger.info("TEST ENDED ====================================");
    Thread.sleep(60000);
  }

  //
  @Test
  @Disabled
  void whenQueryAskedWithinContext_thenAnswerFromTheContext() {
    var response = ollamaRagService.getAnswer("What was Nvidia's 2023 Total Revenue?");
    assertNotNull(response);
    logger.info("Response from RAG LLM: {}", response);
  }

  @Test
  @Disabled
  void whenQueryAskedOutOfContext_thenDontAnswer() {
    var response = ollamaRagService.getAnswer("Why is the sky black?");
    assertEquals("I don't know the answer.", response);
    logger.info("Response from RAG LLM: {}", response);
  }

  @Test
  @RepeatedTest(5)
  void testFalseEvaluation() {
    String userText = "What is the sky blue?";

    ChatResponse response = ChatClient.builder(openAiChatModel)
        .build().prompt()
        .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
        .user(userText)
        .call()
        .chatResponse();

    var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(openAiChatModel));
    EvaluationRequest evaluationRequest = new EvaluationRequest(userText,
        (List<Content>) response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS), response);
    EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

    logger.info("Chat Response from RAG LLM: {}", response);
    logger.info("Evaluation Response from RAG LLM: {}", evaluationResponse);
    assertFalse(evaluationResponse.isPass(), "Response is relevant to the question");

  }

  @Test
  @RepeatedTest(5)
  void testEvaluation() throws InterruptedException {
    String query = "Summarise Apple's 5 year cumulative return";

    ChatResponse response = ChatClient.builder(openAiChatModel)
        .build().prompt()
        .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.query(query).withTopK(3)))
        .user(query)
        .call()
        .chatResponse();

    var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(openAiChatModel));

    EvaluationRequest evaluationRequest = new EvaluationRequest(query,
        (List<Content>) response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS), response);

    EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

    logger.info("Chat Response from RAG LLM: {}", response);
    logger.info("Evaluation Response from RAG LLM: {}", evaluationResponse);
    assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");
    // Thread.sleep(60000); // thread sleep to avoid rate limiting on repeated tests

  }

  @Test
  @RepeatedTest(5)
  void testEvaluation2() throws InterruptedException {
    String query = "How long is Apple's fiscal period";

    ChatResponse response = ChatClient.builder(openAiChatModel)
        .build().prompt()
        .advisors(new QuestionAnswerAdvisor(vectorStore,
            SearchRequest.query(query).withTopK(3)))
        .user(query)
        .call()
        .chatResponse();

    var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(ollamaChatModel));

    EvaluationRequest evaluationRequest = new EvaluationRequest(query,
        (List<Content>) response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS),
        response);
    EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

    logger.info("Chat Response from RAG LLM: {}", response);
    logger.info("Evaluation Response from RAG LLM: {}", evaluationResponse);
    assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");

  }

  @Test
  @RepeatedTest(5)
  void testEvaluation3() throws InterruptedException {
    String query = "What macro economic conditions affected apple";

    ChatResponse response = ChatClient.builder(ollamaChatModel)
        .build().prompt()
        .advisors(new QuestionAnswerAdvisor(vectorStore,
            SearchRequest.query(query).withTopK(3)))
        .user(query)
        .call()
        .chatResponse();

    var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(ollamaChatModel));

    EvaluationRequest evaluationRequest = new EvaluationRequest(query,
        (List<Content>) response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS),
        response);
    EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

    logger.info("Chat Response from RAG LLM: {}", response);
    logger.info("Evaluation Response from RAG LLM: {}", evaluationResponse);
    assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");

  }

  @Test
  @RepeatedTest(5)
  void testEvaluationAgainstOllamaModel() throws InterruptedException {
    String query = "How long is Apple's fiscal period";

    ChatResponse response = ChatClient.builder(openAiChatModel)
        .build().prompt()
        .advisors(new QuestionAnswerAdvisor(vectorStore,
            SearchRequest.query(query).withTopK(3)))
        .user(query)
        .call()
        .chatResponse();

    var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(ollamaChatModel));

    EvaluationRequest evaluationRequest = new EvaluationRequest(query,
        (List<Content>) response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS),
        response);
    EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

    logger.info("Chat Response from RAG LLM: {}", response);
    logger.info("Evaluation Response from RAG LLM: {}", evaluationResponse);
    assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");

  }

  @Test
  @RepeatedTest(5)
  void testEvaluationAgainstOpenAiModel() throws InterruptedException {
    String query = "How long is Apple's fiscal period";

    ChatResponse response = ChatClient.builder(ollamaChatModel)
        .build().prompt()
        .advisors(new QuestionAnswerAdvisor(vectorStore,
            SearchRequest.query(query).withTopK(3)))
        .user(query)
        .call()
        .chatResponse();

    var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(openAiChatModel));

    EvaluationRequest evaluationRequest = new EvaluationRequest(query,
        (List<Content>) response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS),
        response);
    EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

    logger.info("Chat Response from RAG LLM: {}", response);
    logger.info("Evaluation Response from RAG LLM: {}", evaluationResponse);
    assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");

  }
}
