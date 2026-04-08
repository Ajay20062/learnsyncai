package com.learnsyncai.agent.controller;

import com.learnsyncai.agent.dto.AdaptivePlanResponse;
import com.learnsyncai.agent.dto.StudyPlanRequest;
import com.learnsyncai.agent.dto.StudyPlanResponse;
import com.learnsyncai.agent.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plan")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping("/generate")
    public ResponseEntity<StudyPlanResponse> generate(@Valid @RequestBody StudyPlanRequest request) {
        return ResponseEntity.ok(planService.generatePlan(request));
    }

    @GetMapping("/latest")
    public ResponseEntity<StudyPlanResponse> latest() {
        return ResponseEntity.ok(planService.getLatestPlan());
    }

    @GetMapping("/adapt/weekly")
    public ResponseEntity<AdaptivePlanResponse> weeklyAdaptiveRecommendation() {
        return ResponseEntity.ok(planService.getAdaptiveRecommendation());
    }
}
