package com.learnsyncai.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardResponse {
    private int todayTaskCount;
    private int todayCompletedCount;
    private int streakDays;
    private double completionPercent;
    private int missedTasks;
    private List<WeeklyPointDto> weeklyChart;
    private List<TaskDto> todayTasks;
}
