package com.learnsyncai.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReminderResponse {
    private Integer frequencyPerWeek;
    private LocalDateTime lastSent;
}
