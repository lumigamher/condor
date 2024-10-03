package com.projectcondor.condor.service;

import com.projectcondor.condor.config.OpenAIConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private final OpenAIConfig openAIConfig;
    private final RestTemplate restTemplate;

    public OpenAIService(OpenAIConfig openAIConfig, RestTemplate restTemplate) {
        this.openAIConfig = openAIConfig;
        this.restTemplate = restTemplate;
    }

    public String generateChatResponse(String prompt) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAIConfig.getApiKey());
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4"); // Puedes cambiar el modelo aquí si es necesario
        requestBody.put("messages", new Object[]{createMessage("user", prompt)});

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> responseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseBody != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> choices = (Map<String, Object>) ((List<Object>) responseBody.get("choices")).get(0);
            @SuppressWarnings("unchecked")
            Map<String, Object> message = (Map<String, Object>) choices.get("message");
            return (String) message.get("content");
        }

        return null;
    }

    private Map<String, Object> createMessage(String role, String content) {
        Map<String, Object> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }
}
