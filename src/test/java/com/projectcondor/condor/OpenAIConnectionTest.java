package com.projectcondor.condor;

import com.projectcondor.condor.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class OpenAIConnectionTest {

    @Autowired
    private OpenAIService openAIService;

    public static void main(String[] args) {
        SpringApplication.run(OpenAIConnectionTest.class, args);
    }

    @Bean
    @Profile("test")
    CommandLineRunner testOpenAIConnection() {
        return args -> {
            System.out.println("Probando conexión con OpenAI API...");
            String prompt = "Hola, ¿puedes decirme qué día es hoy?";
            String response = openAIService.getResponse(prompt);
            System.out.println("Respuesta de OpenAI: " + response);
            System.out.println("Prueba completada.");
            System.exit(0);
        };
    }
}