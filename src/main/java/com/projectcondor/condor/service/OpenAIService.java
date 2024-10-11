package com.projectcondor.condor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final RestTemplate restTemplate;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String defaultModel;

    @Value("${openai.default.prompt}")
    private String defaultPrompt;

    private final Map<String, List<Map<String, String>>> conversations = new HashMap<>();

    public String chatWithGPT(String conversationId, String message, String model) {
        List<Map<String, String>> conversation = conversations.computeIfAbsent(conversationId, k -> {
            List<Map<String, String>> newConversation = new ArrayList<>();
            newConversation.add(Map.of("role", "system", "content", defaultPrompt));
            return newConversation;
        });

        conversation.add(Map.of("role", "user", "content", message));

        String gptResponse = getResponse(conversation, model != null ? model : defaultModel);
        conversation.add(Map.of("role", "assistant", "content", gptResponse));

        return gptResponse;
    }

    public String getResponse(List<Map<String, String>> messages, String model) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            String jsonResponse = restTemplate.postForObject(apiUrl, request, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            return rootNode.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generando respuesta: " + e.getMessage();
        }
    }

    public void clearConversation(String conversationId) {
        conversations.remove(conversationId);
    }

    //obtener una respuesta simple sin gestión de conversación
    public String getSimpleResponse(String prompt) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        return getResponse(messages, defaultModel);
    }
}