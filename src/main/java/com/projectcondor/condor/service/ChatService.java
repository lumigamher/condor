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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;

    public ResponseEntity<?> sendMessage(Message message) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        message.setUser(user);
        message.setTimestamp(LocalDateTime.now());
        message.setFromAI(false);
        messageRepository.save(message);

        String conversationId = message.getConversationId();
        if (conversationId == null) {
            conversationId = UUID.randomUUID().toString();
            message.setConversationId(conversationId);
        }

        String aiResponse = openAIService.chatWithGPT(conversationId, message.getContent(), null);

        Message aiMessage = new Message();
        aiMessage.setUser(user);
        aiMessage.setContent(aiResponse);
        aiMessage.setTimestamp(LocalDateTime.now());
        aiMessage.setFromAI(true);
        aiMessage.setConversationId(conversationId);
        messageRepository.save(aiMessage);

        return ResponseEntity.ok(aiMessage);
    }

    public ResponseEntity<?> getChatHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Message> messages = messageRepository.findByUserOrderByTimestampAsc(user);
        return ResponseEntity.ok(messages);
    }

    public ResponseEntity<?> clearConversation(String conversationId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clearConversation'");
    }
}