package com.learnsyncai.agent.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReminderUpdateRequest {
    @NotNull
    @Min(1)
    @Max(14)
    private Integer frequencyPerWeek;
}
