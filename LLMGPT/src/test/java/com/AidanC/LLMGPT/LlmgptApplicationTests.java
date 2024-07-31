package com.AidanC.LLMGPT;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LlmgptApplicationTests {
	@Autowired
	private OpenAiChatModel openAiChatModel;

	@Autowired
	private OllamaChatModel ollamaChatModel;

	// @RepeatedTest(5)
	@Test
	void testPreTrainedEvaluation() {
		// Query relative to pre-trained data
		String userText = "What is Nvidia's 2018 total revenue?";

		ChatResponse response = ChatClient.builder(openAiChatModel)
				.build().prompt()
				.user(userText)
				.call()
				.chatResponse();

		var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(openAiChatModel));

		EvaluationRequest evaluationRequest = new EvaluationRequest(userText, List.of(), response);
		EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);
		System.out.println(response);

		System.out.println(evaluationResponse.getMetadata());
		System.out.printf("Test Pre-Trained Data: %s%n ", evaluationResponse);
		assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");
	}

	@Test
	void testOllamaEvaluation() {
		// Query relative to pre-trained data
		String userText = "What is Nvidia's 2018 total revenue?";

		ChatResponse response = ChatClient.builder(ollamaChatModel)
				.build().prompt()
				.user(userText)
				.call()
				.chatResponse();

		var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(ollamaChatModel));

		EvaluationRequest evaluationRequest = new EvaluationRequest(userText, List.of(), response);
		EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);
		System.out.println(response);

		System.out.println(evaluationResponse.getMetadata());
		System.out.printf("Test Pre-Trained Data: %s%n ", evaluationResponse);
		assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");
	}

	// @RepeatedTest(5)
	@Test
	void testOpenAiNewDataEvaluation() {
		// Query relative to new data
		String userText = "What is Nvidia's 2023 total revenue";

		ChatResponse response = ChatClient.builder(openAiChatModel)
				.build().prompt()
				.user(userText)
				.call()
				.chatResponse();

		var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(openAiChatModel));
		EvaluationRequest evaluationRequest = new EvaluationRequest(userText,
				List.of(), response);
		EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);
		System.out.println(response);

		System.out.println(evaluationResponse.getMetadata());
		System.out.printf("Test New Data: %s%n ", evaluationResponse);
		assertFalse(evaluationResponse.isPass(), "Response is relevant to question");
	}

	@Test
	void testOllamaNewDataEvaluation() {
		// Query relative to new data
		String userText = "What is Nvidia's 2023 total revenue";

		ChatResponse response = ChatClient.builder(ollamaChatModel)
				.build().prompt()
				.user(userText)
				.call()
				.chatResponse();

		var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(ollamaChatModel));
		EvaluationRequest evaluationRequest = new EvaluationRequest(userText,
				List.of(), response);
		EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);
		System.out.println(response);

		System.out.println(evaluationResponse.getMetadata());
		System.out.printf("Test New Data: %s%n ", evaluationResponse);
		assertFalse(evaluationResponse.isPass(), "Response is relevant to question");
	}

}
