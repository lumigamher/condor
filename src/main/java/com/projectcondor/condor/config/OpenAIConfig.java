package com.projectcondor.condor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Bean
    RestTemplate restTemplate() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);  // Agregar solo el encabezado de la API Key
            return execution.execute(request, body);
        });
        return restTemplate;
    }

    public String getApiKey() {
        return openaiApiKey;
    }
}
