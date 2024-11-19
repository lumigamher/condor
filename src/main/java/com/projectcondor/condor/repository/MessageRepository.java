package com.projectcondor.condor.repository;

import com.projectcondor.condor.model.Message;
import com.projectcondor.condor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserOrderByTimestampAsc(User user);
    
    List<Message> findByUserAndConversationIdOrderByTimestampAsc(User user, String conversationId);
    
    @Transactional
    long deleteByUserAndConversationId(User user, String conversationId);
}