package com.ederfmatos.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void testeAgendamentosTarefas() {
        System.out.println("Agendamento de tarefas");
    }

}
