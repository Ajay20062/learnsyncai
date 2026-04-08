package com.learnsyncai.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeeklyPointDto {
    private String day;
    private long completedTasks;
}
