package com.projectcondor.condor.controller;

import com.projectcondor.condor.model.Message;
import com.projectcondor.condor.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Message message) {
        return chatService.sendMessage(message);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory() {
        return chatService.getChatHistory();
    }
}