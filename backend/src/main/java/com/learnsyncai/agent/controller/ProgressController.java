package com.learnsyncai.agent.controller;

import com.learnsyncai.agent.dto.DashboardResponse;
import com.learnsyncai.agent.dto.ProgressUpdateRequest;
import com.learnsyncai.agent.service.ProgressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PatchMapping
    public ResponseEntity<Void> update(@Valid @RequestBody ProgressUpdateRequest request) {
        progressService.updateProgress(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(progressService.getDashboardData());
    }
}
