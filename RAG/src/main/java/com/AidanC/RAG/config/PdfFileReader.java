package com.AidanC.RAG.config;

import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

public class PdfFileReader {
    private final VectorStore vectorStore;

    @Value("classpath:/docs/MScProjectHandbook.pdf")
    private Resource pdfResource;

    public PdfFileReader(VectorStore vectorStore, Resource pdfResource) {
        this.vectorStore = vectorStore;
        this.pdfResource = pdfResource;
    }

    public void init() {

        var config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(
                        new ExtractedTextFormatter.Builder()
                                .build())
                .build();

        var pdfReader = new PagePdfDocumentReader(pdfResource, config);
        var textSplitter = new TokenTextSplitter();
        var splitDocument = textSplitter.apply(pdfReader.get());
        vectorStore.accept(textSplitter.apply(pdfReader.get()));
        vectorStore.add(splitDocument);

    }
}
