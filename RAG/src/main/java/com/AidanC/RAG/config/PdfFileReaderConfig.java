package com.AidanC.RAG.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
public class PdfFileReaderConfig {
    private final VectorStore vectorStore;

    private static final Logger log = LoggerFactory.getLogger(PdfFileReaderConfig.class);

    public PdfFileReaderConfig(PgVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Async("Executor")
    public CompletableFuture<Void> addResource(Resource pdfResource) throws InterruptedException {
        return CompletableFuture.runAsync(() -> {
            // Test Results Processing 5 large very large PDFs - some over 150 Pages
            // Concurrect File Process Time - 3.5 Mins
            // Non Concurrect Process Time - 8.5 mins
            long startTime = System.currentTimeMillis();
            String threadName = Thread.currentThread().getName();
            try {
                log.info("[{}] Adding resource: {}", threadName, pdfResource.getFilename());
                PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().build()).build();
                PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(pdfResource,
                        pdfDocumentReaderConfig);
                TokenTextSplitter textSplitter = new TokenTextSplitter();
                vectorStore.accept(textSplitter.apply(pagePdfDocumentReader.get()));
                log.info("[{}] Finished processing file: {}", threadName, pdfResource.getFilename());
            } catch (Exception e) {
                log.error("[{}] Error processing PDF resource: ", threadName, e);
            } finally {
                long endTime = System.currentTimeMillis();
                log.info("[{}] Processing time: {} ms", threadName, (endTime - startTime));
            }
        });
    }
}
