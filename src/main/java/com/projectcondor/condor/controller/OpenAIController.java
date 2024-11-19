package com.projectcondor.condor.controller;

import com.projectcondor.condor.model.Message;
import com.projectcondor.condor.service.ChatService;
import com.projectcondor.condor.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class OpenAIController {

    private final ChatService chatService;
    private final OpenAIService openAIService;
    private static final Logger logger = LoggerFactory.getLogger(OpenAIController.class);

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Message message, Authentication authentication) {
        try {
            logger.info("Recibido mensaje: {}", message.getContent());
            logger.info("Usuario: {}", authentication.getName());

            // Obtener respuesta de OpenAI
            String aiResponse = openAIService.chatWithGPT(
                message.getConversationId(),
                message.getContent(),
                null
            );

            // Crear mensaje de respuesta del AI
            Message aiMessage = new Message();
            aiMessage.setContent(aiResponse);
            aiMessage.setFromAI(true);
            aiMessage.setConversationId(message.getConversationId());

            // Crear respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("userMessage", message);
            response.put("aiMessage", aiMessage);
            response.put("conversationId", message.getConversationId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error procesando mensaje: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", java.time.LocalDateTime.now()
                ));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory(Authentication authentication) {
        try {
            logger.info("Obteniendo historial para usuario: {}", authentication.getName());
            return chatService.getChatHistory();
        } catch (Exception e) {
            logger.error("Error obteniendo historial: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity<?> clearConversation(
            @PathVariable String conversationId,
            Authentication authentication) {
        try {
            logger.info("Limpiando conversación: {} para usuario: {}", 
                conversationId, authentication.getName());
            return chatService.clearConversation(conversationId);
        } catch (Exception e) {
            logger.error("Error limpiando conversación: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/conversation/new")
    public ResponseEntity<?> startNewConversation(Authentication authentication) {
        try {
            String conversationId = java.util.UUID.randomUUID().toString();
            logger.info("Nueva conversación: {} para usuario: {}", 
                conversationId, authentication.getName());
            
            return ResponseEntity.ok(Map.of(
                "conversationId", conversationId,
                "timestamp", java.time.LocalDateTime.now()
            ));
        } catch (Exception e) {
            logger.error("Error creando nueva conversación: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
}