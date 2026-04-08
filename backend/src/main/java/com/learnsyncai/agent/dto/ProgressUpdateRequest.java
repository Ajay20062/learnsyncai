package com.learnsyncai.agent.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgressUpdateRequest {
    @NotNull
    private Long taskId;

    @NotNull
    private Boolean completed;
}
