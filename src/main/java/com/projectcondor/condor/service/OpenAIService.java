package com.projectcondor.condor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    public String getResponse(String prompt) {
        // Configurar los encabezados
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + apiKey);

        // Cuerpo de la solicitud JSON
        String requestBody = "{\n" +
                "  \"model\": \"" + model + "\",\n" +
                "  \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}],\n" +
                "  \"max_tokens\": 100\n" +
                "}";

        // Crear la entidad HTTP con el cuerpo de la solicitud y los encabezados
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Realizar la solicitud
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
