package com.AidanC.RAG;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import com.AidanC.RAG.config.PdfFileReaderConfig;
import com.AidanC.RAG.config.TikaFileReaderConfig;
import org.springframework.shell.command.annotation.CommandScan;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
@CommandScan
@SpringBootApplication
public class RagApplication implements CommandLineRunner {

    private final TikaFileReaderConfig tikaFileReaderConfig;

    private final PdfFileReaderConfig pdfFileReaderConfig;

    @Value("classpath:/docs/MScProjectHandbook.pdf")
    private Resource pdfResource;

    @Value("classpath:/docs/Test.csv")
    private Resource csvResource;



@Value("classpath:/docs/bankstatements.csv")
private Resource bankStatement;

    @Value("src/main/resources/docs/")
    private String directoryPath;


    public RagApplication(TikaFileReaderConfig tikaFileReaderConfig, PdfFileReaderConfig pdfFileReaderConfig) {
        this.tikaFileReaderConfig = tikaFileReaderConfig;
        this.pdfFileReaderConfig = pdfFileReaderConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
//         pdfFileReaderConfig.addResource(pdfResource);
//         tikaFileReaderConfig.addResource(bankStatement);
//         tikaFileReaderConfig.addResource(csvResource);
//        Path path = Paths.get("src/main/resources/docs/Test.csv");
//        tikaFileReaderConfig.addResource(path);
//        tikaFileReaderConfig.processFilesInDirectory(directoryPath);
//        commandLineRunner();

    }
//    @Bean
//    CommandLineRunner commandLineRunner() {
//        return args -> System.out.println("hello ");
//    }
}
