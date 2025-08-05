package com.AidanC.RAG;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.AidanC.RAG.config.PdfFileReaderConfig;
import com.AidanC.RAG.controller.RAGController;
import com.AidanC.RAG.service.RAGService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(RAGController.class)
public class RagApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RAGService ragService;

    @MockBean
    private PdfFileReaderConfig pdfFileReaderConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testChatEndpoint_Success() throws Exception {
        String message = "What is Apple's revenue?";
        String expectedResponse = "Apple's total revenue for 2023 was $383.285 billion.";
        when(ragService.getAnswer(anyString())).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"" + message + "\""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(expectedResponse));
    }

    @Test
    void testChatEndpoint_EmptyMessage() throws Exception {
        String emptyMessage = "";
        String expectedResponse = "Please provide a valid question.";
        when(ragService.getAnswer(anyString())).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"" + emptyMessage + "\""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(expectedResponse));
    }

    @Test
    void testUploadEndpoint_Success() throws Exception {
        String requestBody = "[{\"filePath\": \"Apple_AnnualReport_2023.pdf\"}]";
        doNothing().when(pdfFileReaderConfig).addResource(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Files Are Being Processed - Please Be Patient This May Take A While."));
    }

    @Test
    void testUploadEndpoint_FileNotFound() throws Exception {
        String requestBody = "[{\"filePath\": \"NonExistentFile.pdf\"}]";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("File does not exist: docs/NonExistentFile.pdf"));
    }

    @Test
    void testUploadEndpoint_ProcessingError() throws Exception {
        String requestBody = "invalid-json";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testRefreshDbEndpoint_Success() throws Exception {
        doNothing().when(ragService).clearDatabase();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/refreshdb"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Database cleared successfully."));
    }

    @Test
    void testRefreshDbEndpoint_Error() throws Exception {
        doThrow(new RuntimeException("Database error")).when(ragService).clearDatabase();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/refreshdb"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("An error occurred while clearing the database: Database error"));
    }

    @Test
    void testUploadEndpoint_MultipleFiles() throws Exception {
        String requestBody = "[{\"filePath\": \"Apple_AnnualReport_2023.pdf\"}, {\"filePath\": \"Apple_AnnualReport_2023.pdf\"}]";
        doNothing().when(pdfFileReaderConfig).addResource(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Files Are Being Processed - Please Be Patient This May Take A While."));
    }
}
