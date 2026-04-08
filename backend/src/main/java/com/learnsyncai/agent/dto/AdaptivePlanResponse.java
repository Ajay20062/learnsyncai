package com.learnsyncai.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdaptivePlanResponse {
    private double completionPercent;
    private String recommendation;
}
