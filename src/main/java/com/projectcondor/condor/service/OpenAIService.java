package com.projectcondor.condor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    private final RestTemplate restTemplate;
    
    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String defaultModel;

    @Value("${openai.default.prompt}")
    private String defaultPrompt;

    private final Map<String, List<Map<String, String>>> conversations = new HashMap<>();

    public String chatWithGPT(String conversationId, String message, String model) {
        try {
            List<Map<String, String>> conversation = conversations.computeIfAbsent(
                conversationId,
                k -> new ArrayList<>(Collections.singletonList(
                    Map.of("role", "system", "content", defaultPrompt)
                ))
            );

            // Si es una nueva conversación, verificar que el prompt esté presente
            if (conversation.size() == 1) {
                logger.info("Iniciando nueva conversación con prompt: {}", defaultPrompt);
            }

            // Agregar el mensaje del usuario
            conversation.add(Map.of("role", "user", "content", message));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model != null ? model : defaultModel);
            requestBody.put("messages", conversation);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 150);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                request,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                String aiResponse = rootNode.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

                // Agregar la respuesta a la conversación
                conversation.add(Map.of("role", "assistant", "content", aiResponse));
                return aiResponse;
            } else {
                throw new RuntimeException("Error en la respuesta de OpenAI");
            }
        } catch (Exception e) {
            logger.error("Error procesando mensaje", e);
            clearConversation(conversationId); // Limpiar conversación en caso de error
            return "Lo siento, ocurrió un error al procesar tu mensaje. Por favor, intenta nuevamente.";
        }
    }

    public void clearConversation(String conversationId) {
        conversations.remove(conversationId);
        logger.info("Conversación limpiada: {}", conversationId);
    }
}