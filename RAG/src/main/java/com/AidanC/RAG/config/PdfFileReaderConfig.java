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
import org.springframework.stereotype.Component;

@Component
public class PdfFileReaderConfig {
    private final VectorStore vectorStore;

    private static final Logger log = LoggerFactory.getLogger(PdfFileReaderConfig.class);

    public PdfFileReaderConfig(PgVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void addResource(Resource pdfResource) {
        log.info("adding resource...");
        PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().build()).build();
        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(pdfResource, pdfDocumentReaderConfig);
        TokenTextSplitter textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(pagePdfDocumentReader.get()));

    }
}
