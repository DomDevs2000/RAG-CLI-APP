package com.AidanC.RAG;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

import com.AidanC.RAG.config.PdfFileReaderConfig;
import com.AidanC.RAG.config.TikaFileReaderConfig;

@SpringBootApplication
public class RagApplication implements CommandLineRunner {

    private final TikaFileReaderConfig tikaFileReaderConfig;

    private final PdfFileReaderConfig pdfFileReaderConfig;

    @Value("classpath:/docs/MScProjectHandbook.pdf")
    private Resource pdfResource;

    @Value("classpath:/docs/Test.csv")
    private Resource csvResource;

    @Value("classpath:/docs/Disney-Earnings-q4fy23.pdf")
    private Resource earnings;

    public RagApplication(TikaFileReaderConfig tikaFileReaderConfig, PdfFileReaderConfig pdfFileReaderConfig) {
        this.tikaFileReaderConfig = tikaFileReaderConfig;
        this.pdfFileReaderConfig = pdfFileReaderConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // pdfFileReaderConfig.addResource(pdfResource);
        // large csv file already added to db
        // tikaFileReaderConfig.addResource(csvResource);
        pdfFileReaderConfig.addResource(earnings);
    }
}
