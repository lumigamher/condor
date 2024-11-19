package com.projectcondor.condor.service;

import com.projectcondor.condor.model.Message;
import com.projectcondor.condor.model.User;
import com.projectcondor.condor.repository.MessageRepository;
import com.projectcondor.condor.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;

    public ResponseEntity<?> sendMessage(Message message) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            message.setUser(user);
            message.setTimestamp(LocalDateTime.now());
            message.setFromAI(false);

            if (message.getConversationId() == null) {
                message.setConversationId(UUID.randomUUID().toString());
            }

            Message savedUserMessage = messageRepository.save(message);

            String aiResponse = openAIService.chatWithGPT(
                message.getConversationId(), 
                message.getContent(),
                null
            );

            Message aiMessage = new Message();
            aiMessage.setUser(user);
            aiMessage.setContent(aiResponse);
            aiMessage.setTimestamp(LocalDateTime.now());
            aiMessage.setFromAI(true);
            aiMessage.setConversationId(message.getConversationId());
            Message savedAiMessage = messageRepository.save(aiMessage);

            Map<String, Object> response = new HashMap<>();
            response.put("userMessage", savedUserMessage);
            response.put("aiMessage", savedAiMessage);
            response.put("conversationId", message.getConversationId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    public ResponseEntity<?> getChatHistory() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Message> messages = messageRepository.findByUserOrderByTimestampAsc(user);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    public ResponseEntity<?> clearConversation(String conversationId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            messageRepository.deleteByUserAndConversationId(user, conversationId);
            openAIService.clearConversation(conversationId);

            return ResponseEntity.ok(Map.of(
                "message", "Conversación limpiada exitosamente",
                "conversationId", conversationId
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    public ResponseEntity<?> startNewConversation() {
        try {
            String conversationId = UUID.randomUUID().toString();
            return ResponseEntity.ok(Map.of(
                "conversationId", conversationId,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}