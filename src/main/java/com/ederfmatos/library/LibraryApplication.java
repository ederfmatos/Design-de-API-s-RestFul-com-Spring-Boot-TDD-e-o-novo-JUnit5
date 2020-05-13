package com.ederfmatos.library;

import com.ederfmatos.library.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class LibraryApplication {

    @Autowired
    private EmailService emailService;

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            List<String> emails = List.of("ederfmatos@gmail.com");

            emailService.sendMails("Testando serviço de emails", emails);
        };
    }

}
