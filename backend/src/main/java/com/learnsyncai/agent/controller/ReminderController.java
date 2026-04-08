package com.learnsyncai.agent.controller;

import com.learnsyncai.agent.dto.ReminderResponse;
import com.learnsyncai.agent.dto.ReminderUpdateRequest;
import com.learnsyncai.agent.service.ReminderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reminder")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping
    public ResponseEntity<ReminderResponse> getReminder() {
        return ResponseEntity.ok(reminderService.getReminder());
    }

    @PutMapping
    public ResponseEntity<ReminderResponse> updateReminder(@Valid @RequestBody ReminderUpdateRequest request) {
        return ResponseEntity.ok(reminderService.updateReminder(request));
    }

    @PostMapping("/auto-adjust")
    public ResponseEntity<ReminderResponse> autoAdjust() {
        return ResponseEntity.ok(reminderService.autoAdjustReminderForCurrentUser());
    }
}
