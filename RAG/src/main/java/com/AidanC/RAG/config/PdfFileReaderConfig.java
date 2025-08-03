package com.AidanC.RAG.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class PdfFileReaderConfig {
    private final PgVectorStore vectorStore;

    private static final Logger log = LoggerFactory.getLogger(PdfFileReaderConfig.class);

    public PdfFileReaderConfig(PgVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void addResource(Resource pdfResource) {
        long startTime = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();
        try {
            var pdfFileName = pdfResource.getFilename();
            log.info("[{}] Adding resource: {}", threadName, pdfFileName);

            String fullText = extractTextWithPdfBox(pdfResource);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("filename", pdfFileName);
            metadata.put("source", pdfResource.getFilename());

            Document document = new Document(fullText, metadata);
            List<Document> documents = List.of(document);

            TokenTextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(documents);

            for (Document splitDoc : splitDocuments) {
                splitDoc.getMetadata().put("filename", pdfFileName);
                splitDoc.getMetadata().put("source", pdfFileName);
            }

            vectorStore.accept(splitDocuments);
            log.info("[{}] Finished processing file: {} - extracted {} chunks", threadName, pdfFileName,
                    splitDocuments.size());
        } catch (Exception e) {
            log.error("[{}] Error processing PDF resource: ", threadName, e);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("[{}] Processing time: {} ms", threadName, (endTime - startTime));
        }
    }

    private String extractTextWithPdfBox(Resource pdfResource) throws IOException {
        try (var inputStream = pdfResource.getInputStream();
             var randomAccessRead = new RandomAccessReadBuffer(inputStream);
             PDDocument document = Loader.loadPDF(randomAccessRead)) {
            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setSortByPosition(true);
            return textStripper.getText(document);
        }
    }
}
