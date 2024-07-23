package com.AidanC.RAG;

import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RagApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        System.out.println("Application Running...");
    }
}
