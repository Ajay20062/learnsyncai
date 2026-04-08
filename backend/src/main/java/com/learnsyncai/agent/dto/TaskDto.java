package com.learnsyncai.agent.dto;

import com.learnsyncai.agent.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TaskDto {
    private Long id;
    private Integer dayNumber;
    private String description;
    private String revisionNote;
    private TaskStatus status;
}
