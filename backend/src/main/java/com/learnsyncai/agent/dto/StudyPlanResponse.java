package com.learnsyncai.agent.dto;

import com.learnsyncai.agent.model.SkillLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StudyPlanResponse {
    private Long id;
    private String goal;
    private Integer durationDays;
    private Integer dailyHours;
    private SkillLevel difficulty;
    private List<TaskDto> tasks;
}
