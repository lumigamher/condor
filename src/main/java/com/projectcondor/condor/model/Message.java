package com.projectcondor.condor.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean fromAI;

    @Column(nullable = false)
    private String conversationId;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Constructor para mensajes nuevos
    public Message(String content, boolean fromAI, String conversationId) {
        this.content = content;
        this.fromAI = fromAI;
        this.conversationId = conversationId;
        this.timestamp = LocalDateTime.now();
    }
}