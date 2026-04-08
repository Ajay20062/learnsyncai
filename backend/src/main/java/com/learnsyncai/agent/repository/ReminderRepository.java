package com.learnsyncai.agent.repository;

import com.learnsyncai.agent.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    Optional<Reminder> findByUserId(Long userId);
}
