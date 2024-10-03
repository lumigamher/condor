package com.projectcondor.condor.repository;

import com.projectcondor.condor.model.Message;
import com.projectcondor.condor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserOrderByTimestampAsc(User user);
}