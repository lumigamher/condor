package com.projectcondor.condor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import java.time.Duration;
import java.util.*;

@Service
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String defaultModel;

    private final Map<String, List<Map<String, String>>> conversations = new HashMap<>();

    public OpenAIService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .build();
        this.objectMapper = new ObjectMapper();
    }

    public String chatWithGPT(String conversationId, String message, String model) {
        logger.info("Iniciando solicitud a OpenAI para conversación: {}", conversationId);
        
        try {
            List<Map<String, String>> conversation = conversations.computeIfAbsent(
                conversationId,
                k -> new ArrayList<>(Collections.singletonList(
                    Map.of("role", "system", "content", "Eres un asistente amigable.")
                ))
            );

            conversation.add(Map.of("role", "user", "content", message));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model != null ? model : defaultModel);
            requestBody.put("messages", conversation);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 150);
            
            logger.debug("Enviando solicitud a OpenAI: {}", requestBody);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                request,
                String.class
            );

            logger.debug("Respuesta recibida de OpenAI: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                String aiResponse = rootNode.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

                conversation.add(Map.of("role", "assistant", "content", aiResponse));
                
                logger.info("Respuesta procesada exitosamente");
                return aiResponse;
            } else {
                logger.error("Error en la respuesta de OpenAI: {}", response.getStatusCode());
                return "Lo siento, hubo un problema al procesar tu mensaje.";
            }

        } catch (Exception e) {
            logger.error("Error al procesar mensaje con OpenAI", e);
            return "Lo siento, ocurrió un error: " + e.getMessage();
        }
    }

    public void clearConversation(String conversationId) {
        conversations.remove(conversationId);
        logger.info("Conversación limpiada: {}", conversationId);
    }
}