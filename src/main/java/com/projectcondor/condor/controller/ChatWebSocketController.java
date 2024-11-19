package com.projectcondor.condor.controller;

import com.projectcondor.condor.model.Message;
import com.projectcondor.condor.service.OpenAIService;
import com.projectcondor.condor.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final OpenAIService openAIService;
    private final SimpMessagingTemplate messagingTemplate;
    @SuppressWarnings("unused")
    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void handleChatMessage(@Payload Message message, Authentication authentication) {
        try {
            // Obtener respuesta de OpenAI usando el servicio existente
            String aiResponse = openAIService.chatWithGPT(
                message.getConversationId(), 
                message.getContent(),
                null
            );

            // Crear mensaje de respuesta
            Message aiMessage = new Message();
            aiMessage.setContent(aiResponse);
            aiMessage.setFromAI(true);
            aiMessage.setConversationId(message.getConversationId());

            // Enviar respuesta al usuario
            messagingTemplate.convertAndSendToUser(
                authentication.getName(),
                "/queue/messages",
                aiMessage
            );

        } catch (Exception e) {
            // Enviar error al usuario
            messagingTemplate.convertAndSendToUser(
                authentication.getName(),
                "/queue/errors",
                e.getMessage()
            );
        }
    }

    @MessageMapping("/chat.connect")
    public void handleConnect(Authentication authentication) {
        messagingTemplate.convertAndSendToUser(
            authentication.getName(),
            "/queue/connect",
            "Connected successfully"
        );
    }
}