package com.projectcondor.condor.controller;

import com.projectcondor.condor.model.Message;
import com.projectcondor.condor.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class OpenAIController {

    private final ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Message message) {
        return chatService.sendMessage(message);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory() {
        return chatService.getChatHistory();
    }

    @DeleteMapping("/clear/{conversationId}")
    public ResponseEntity<?> clearConversation(@PathVariable String conversationId) {
        return chatService.clearConversation(conversationId);
    }

    @PostMapping("/new")
    public ResponseEntity<?> startNewConversation() {
        // Este método podría simplemente devolver un nuevo conversationId
        String newConversationId = java.util.UUID.randomUUID().toString();
        return ResponseEntity.ok(newConversationId);
    }
}