package com.AidanC.RAG;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.ai.model.Content;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import com.AidanC.RAG.config.PdfFileReaderConfig;

@SpringBootTest
public class RagApplicationTests {

	@Autowired
	private ChatModel chatModel;

	@Autowired
	private PgVectorStore vectorStore;

	@Value("classpath:/docs/Apple_AnnualReport_2023.pdf")
	private Resource pdfResource;

	@Test
	void testValidEvaluation() {
		// Query relative to document
		String userText = "What is Apple's 2023 total revenue?";

		ChatResponse response = ChatClient.builder(chatModel)
				.build().prompt()
				.advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
				.user(userText)
				.call()
				.chatResponse();

		var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(chatModel));
		EvaluationRequest evaluationRequest = new EvaluationRequest(userText,
				(List<Content>) response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS), response);
		EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);
		System.out.println(evaluationResponse);
		assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");

	}

	@Test
	void testFalseEvaluation() {
		// query not relevant to document;
		String userText = "What is Apple's 2019 revenue?";

		ChatResponse response = ChatClient.builder(chatModel)
				.build().prompt()
				.advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
				.user(userText)
				.call()
				.chatResponse();

		var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(chatModel));
		EvaluationRequest evaluationRequest = new EvaluationRequest(userText,
				(List<Content>) response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS), response);
		EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);
		System.out.println(evaluationResponse);
		assertFalse(evaluationResponse.isPass(), "Response is not relevant to the question");

	}

}
