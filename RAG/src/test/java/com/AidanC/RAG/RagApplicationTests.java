package com.AidanC.RAG;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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
  private RAGService ragService;

  @Value("classpath:/docs/Apple_AnnualReport_2023.pdf")
  private Resource pdfResource;

  // @RepeatedTest(10)
  // @Test
  // void testOpenAiValidEvaluation() {
  // // Query relative to document
  // String userText = "What is Nvidia's 2023 total revenue?";
  //
  // ChatResponse response = ChatClient.builder(openAiChatModel)
  // .build()
  // .prompt()
  // .advisors(new QuestionAnswerAdvisor(vectorStore,
  // SearchRequest.defaults().withTopK(3)))
  // .user(userText)
  // .call()
  // .chatResponse();
  //
  // var relevancyEvaluator = new
  // RelevancyEvaluator(ChatClient.builder(openAiChatModel));
  // EvaluationRequest evaluationRequest = new EvaluationRequest(
  // userText,
  // (List<Content>)
  // response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS),
  // response);
  // EvaluationResponse evaluationResponse =
  // relevancyEvaluator.evaluate(evaluationRequest);
  // System.out.println(response);
  // System.out.printf("Test Data Relevant To Document: %s%n ",
  // evaluationResponse);
  // assertTrue(evaluationResponse.isPass(), "Response is not relevant to the
  // question");
  // }
  //
  // @Test
  // void testOllamaValidEvaluation() {
  // // Query relative to document
  // String userText = "What is Nvidia's 2023 total revenue?";
  //
  // ChatResponse response = ChatClient.builder(ollamaChatModel)
  // .build()
  // .prompt()
  // .advisors(new QuestionAnswerAdvisor(vectorStore,
  // SearchRequest.defaults().withTopK(3)))
  // .user(userText)
  // .call()
  // .chatResponse();
  //
  // var relevancyEvaluator = new
  // RelevancyEvaluator(ChatClient.builder(ollamaChatModel));
  // EvaluationRequest evaluationRequest = new EvaluationRequest(
  // userText,
  // (List<Content>)
  // response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS),
  // response);
  // EvaluationResponse evaluationResponse =
  // relevancyEvaluator.evaluate(evaluationRequest);
  // System.out.println(response);
  // System.out.printf("Test Data Relevant To Document: %s%n ",
  // evaluationResponse);
  // assertTrue(evaluationResponse.isPass(), "Response is not relevant to the
  // question");
  // }
  //
  // // @RepeatedTest(10)
  // @Test
  // void testOpenAiFalseEvaluation() {
  // // query not relevant to document;
  // String falseData = "What was Nvidia's 2011 revenue?";
  //
  // ChatResponse response = ChatClient.builder(openAiChatModel)
  // .build()
  // .prompt()
  // .advisors(new QuestionAnswerAdvisor(vectorStore,
  // SearchRequest.defaults().withTopK(3)))
  // .user(falseData)
  // .call()
  // .chatResponse();
  // var relevancyEvaluator = new
  // RelevancyEvaluator(ChatClient.builder(openAiChatModel));
  // EvaluationRequest evaluationRequest = new EvaluationRequest(
  // falseData,
  // (List<Content>)
  // response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS),
  // response);
  // EvaluationResponse evaluationResponse =
  // relevancyEvaluator.evaluate(evaluationRequest);
  //
  // System.out.println(response);
  //
  // System.out.println(evaluationResponse.getMetadata());
  // System.out.printf("Test Data Not Relevant To Document: %s%n ",
  // evaluationResponse);
  // assertFalse(evaluationResponse.isPass(), "Response is relevant to the
  // question");
  // }
  //
  // @Test
  // void testOllamaFalseEvaluation() {
  // // query not relevant to document;
  // String falseData = "What was Nvidia's 2011 revenue?";
  //
  // ChatResponse response = ChatClient.builder(ollamaChatModel)
  // .build()
  // .prompt()
  // .advisors(new QuestionAnswerAdvisor(vectorStore,
  // SearchRequest.defaults().withTopK(3)))
  // .user(falseData)
  // .call()
  // .chatResponse();
  // var relevancyEvaluator = new
  // RelevancyEvaluator(ChatClient.builder(ollamaChatModel));
  // EvaluationRequest evaluationRequest = new EvaluationRequest(
  // falseData,
  // (List<Content>)
  // response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS),
  // response);
  // EvaluationResponse evaluationResponse =
  // relevancyEvaluator.evaluate(evaluationRequest);
  //
  // System.out.println(response);
  //
  // System.out.println(evaluationResponse.getMetadata());
  // System.out.printf("Test Data Not Relevant To Document: %s%n ",
  // evaluationResponse);
  // assertFalse(evaluationResponse.isPass(), "Response is relevant to the
  // question");
  // }
  //
  @Test
  void whenQueryAskedWithinContext_thenAnswerFromTheContext() {
    var response = ragService.getAnswer("What was Nvidia's 2023 Total Revenue?");
    assertNotNull(response);
    logger.info("Response from RAG LLM: {}", response);
  }

  @Test
  void whenQueryAskedOutOfContext_thenDontAnswer() {
    var response = ragService.getAnswer("Why is the sky blue?");
    assertEquals("I don't know the answer.", response);
    logger.info("Response from RAG LLM: {}", response);
  }

}
