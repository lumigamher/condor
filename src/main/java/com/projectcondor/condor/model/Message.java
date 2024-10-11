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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private boolean fromAI;

    @Column(nullable = false)
    private String conversationId;

    // You might want to add a constructor with essential fields
    public Message(User user, String content, boolean fromAI, String conversationId) {
        this.user = user;
        this.content = content;
        this.fromAI = fromAI;
        this.conversationId = conversationId;
        this.timestamp = LocalDateTime.now();
    }
}