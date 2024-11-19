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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class OpenAIController {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIController.class);
    
    private final ChatService chatService;
    private final OpenAIService openAIService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Message message, Authentication authentication) {
        try {
            // Si no hay conversationId, crear uno
            if (message.getConversationId() == null) {
                message.setConversationId(UUID.randomUUID().toString());
            }

            String aiResponse = openAIService.chatWithGPT(
                message.getConversationId(),
                message.getContent(),
                null
            );

            Message aiMessage = new Message();
            aiMessage.setContent(aiResponse);
            aiMessage.setFromAI(true);
            aiMessage.setConversationId(message.getConversationId());
            aiMessage.setTimestamp(LocalDateTime.now());

            Map<String, Object> response = new HashMap<>();
            response.put("userMessage", message);
            response.put("aiMessage", aiMessage);
            response.put("conversationId", message.getConversationId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error en sendMessage", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory(Authentication authentication) {
        try {
            return chatService.getChatHistory();
        } catch (Exception e) {
            logger.error("Error en getChatHistory", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/conversation")
    public ResponseEntity<?> startNewConversation() {
        try {
            String conversationId = UUID.randomUUID().toString();
            Map<String, Object> response = new HashMap<>();
            response.put("conversationId", conversationId);
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al crear nueva conversación", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity<?> clearConversation(@PathVariable String conversationId) {
        try {
            openAIService.clearConversation(conversationId);
            return ResponseEntity.ok(Map.of(
                "message", "Conversación limpiada exitosamente",
                "conversationId", conversationId
            ));
        } catch (Exception e) {
            logger.error("Error al limpiar conversación", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
}