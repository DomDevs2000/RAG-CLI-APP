package com.AidanC.RAG.config;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
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
  private final PgVectorStore vectorStore;

  private static final Logger log = LoggerFactory.getLogger(PdfFileReaderConfig.class);

  public PdfFileReaderConfig(PgVectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  public void addResource(Resource pdfResource) {
    // Test Results Processing 5 large very large PDFs - some over 150 Pages
    // Completeable Future Concurrect File Process Time -> 3-3.5 Mins
    // Normal Async File Process Time -> 3 mins
    // Non Concurrect File Process Time -> 8.5 mins
    // Virtual Thread File Process Time -> 2.5 mins
    long startTime = System.currentTimeMillis();
    String threadName = Thread.currentThread().getName();
    try {
      log.info("[{}] Adding resource: {}", threadName, pdfResource.getFilename());
      PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
          .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().build())
          .build();
      PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(pdfResource, pdfDocumentReaderConfig);
      TokenTextSplitter textSplitter = new TokenTextSplitter();
      List<Document> splitDocuments = textSplitter.apply(pagePdfDocumentReader.get());
      for (Document splitDocument : splitDocuments) {
        splitDocument.getMetadata().put("filename", pdfResource.getFilename());
        splitDocument.getMetadata().put("version", 1);
      }
      vectorStore.accept(splitDocuments);
      log.info("[{}] Finished processing file: {}", threadName, pdfResource.getFilename());
    } catch (Exception e) {
      log.error("[{}] Error processing PDF resource: ", threadName, e);
    } finally {
      long endTime = System.currentTimeMillis();
      log.info("[{}] Processing time: {} ms", threadName, (endTime - startTime));
    }
  }
}
