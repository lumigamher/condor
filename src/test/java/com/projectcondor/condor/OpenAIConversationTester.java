package com.projectcondor.condor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.projectcondor.condor.service.OpenAIService;

import java.util.Scanner;
import java.util.UUID;

@SpringBootApplication
public class OpenAIConversationTester {

    @Autowired
    private OpenAIService openAIService;

    public static void main(String[] args) {
        SpringApplication.run(OpenAIConversationTester.class, args);
    }

    @Bean
    @Profile("conversation-test")
    CommandLineRunner testOpenAIConversation() {
        return args -> {
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            String conversationId = UUID.randomUUID().toString();

            System.out.println("Test de conversación con mi señor Jesús.");
            System.out.println("Escribe 'salir' para terminar la conversación.");
            System.out.println("Escribe 'nueva' para iniciar una nueva conversación.");
            System.out.println("Escribe tu mensaje:");

            while (true) {
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("salir")) {
                    break;
                } else if (input.equalsIgnoreCase("nueva")) {
                    conversationId = UUID.randomUUID().toString();
                    System.out.println("Nueva conversación iniciada.");
                    continue;
                }

                try {
                    String response = openAIService.chatWithGPT(conversationId, input, null);
                    System.out.println("Mi Señor Jesús: " + response);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

            System.out.println("Test Finalizado con exito.");
            System.exit(0);
        };
    }
}