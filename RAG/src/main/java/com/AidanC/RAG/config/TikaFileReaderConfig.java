package com.AidanC.RAG.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class TikaFileReaderConfig {

    private final PgVectorStore vectorStore;

    private static final Logger log = LoggerFactory.getLogger(TikaFileReaderConfig.class);

    public TikaFileReaderConfig(PgVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void processFilesInDirectory(String directoryPath) throws IOException {
        Path dir = Paths.get(directoryPath);
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(Files::isRegularFile)
                    .forEach(this::addResource);
        }
        log.info("Finished processing all files in directory: " + directoryPath);
    }

    public void addResource(Path path) throws IllegalArgumentException {
        log.info("Adding Resource...");
        Resource resource = new FileSystemResource(path.toFile());
        TikaDocumentReader tikaDocumentReaderConfig = new TikaDocumentReader(resource);
        TokenTextSplitter textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(tikaDocumentReaderConfig.get()));
        log.info("Finished processing file: {}", path);
    }

    public void addResource(Resource resource) {
        log.info("Adding Resource...");
        TikaDocumentReader tikaDocumentReaderConfig = new TikaDocumentReader(resource);
        TokenTextSplitter textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(tikaDocumentReaderConfig.get()));
        log.info("Finished processing file: {}", resource);

    }
}
