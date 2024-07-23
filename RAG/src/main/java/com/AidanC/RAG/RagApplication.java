package com.AidanC.RAG;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableAsync;

import com.AidanC.RAG.config.PdfFileReaderConfig;
import com.AidanC.RAG.config.TikaFileReaderConfig;

import java.io.IOException;

@SpringBootApplication
@EnableAsync
public class RagApplication implements CommandLineRunner {

    private final TikaFileReaderConfig tikaFileReaderConfig;

    private final PdfFileReaderConfig pdfFileReaderConfig;

    @Value("classpath:/docs/bank_statement.csv")
    private Resource csvResource;

    @Value("classpath:/docs/McDonalds_AnnualReport_2023.pdf")
    private Resource report;

    @Value("classpath:/docs/test_statement.csv")
    private Resource bankStatement;

    public RagApplication(TikaFileReaderConfig tikaFileReaderConfig, PdfFileReaderConfig pdfFileReaderConfig) {
        this.tikaFileReaderConfig = tikaFileReaderConfig;
        this.pdfFileReaderConfig = pdfFileReaderConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        System.out.println("Application Running...");
        // pdfFileReaderConfig.addResource(report);
        // tikaFileReaderConfig.addResource(csvResource);
    }
}
