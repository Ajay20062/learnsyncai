package com.learnsyncai.agent.service;

import com.learnsyncai.agent.dto.ReminderResponse;
import com.learnsyncai.agent.dto.ReminderUpdateRequest;
import com.learnsyncai.agent.exception.ResourceNotFoundException;
import com.learnsyncai.agent.model.Reminder;
import com.learnsyncai.agent.model.User;
import com.learnsyncai.agent.repository.ProgressLogRepository;
import com.learnsyncai.agent.repository.ReminderRepository;
import com.learnsyncai.agent.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final ProgressLogRepository progressLogRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final AiService aiService;
    private final EmailService emailService;

    public ReminderService(ReminderRepository reminderRepository,
                           ProgressLogRepository progressLogRepository,
                           CurrentUserService currentUserService,
                           UserRepository userRepository,
                           AiService aiService,
                           EmailService emailService) {
        this.reminderRepository = reminderRepository;
        this.progressLogRepository = progressLogRepository;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.aiService = aiService;
        this.emailService = emailService;
    }

    public ReminderResponse getReminder() {
        User user = currentUserService.getCurrentUser();
        Reminder reminder = reminderRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reminder settings not found"));
        return new ReminderResponse(reminder.getFrequencyPerWeek(), reminder.getLastSent());
    }

    @Transactional
    public ReminderResponse updateReminder(ReminderUpdateRequest request) {
        User user = currentUserService.getCurrentUser();
        Reminder reminder = reminderRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reminder settings not found"));
        reminder.setFrequencyPerWeek(request.getFrequencyPerWeek());
        Reminder updated = reminderRepository.save(reminder);
        return new ReminderResponse(updated.getFrequencyPerWeek(), updated.getLastSent());
    }

    @Transactional
    public ReminderResponse autoAdjustReminderForCurrentUser() {
        User user = currentUserService.getCurrentUser();
        Reminder reminder = reminderRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reminder settings not found"));
        adjustFrequency(user, reminder);
        Reminder updated = reminderRepository.save(reminder);
        return new ReminderResponse(updated.getFrequencyPerWeek(), updated.getLastSent());
    }

    @Scheduled(cron = "${app.reminder.cron}")
    @Transactional
    public void sendDailyReminders() {
        for (User user : userRepository.findAll()) {
            Reminder reminder = reminderRepository.findByUserId(user.getId()).orElse(null);
            if (reminder == null) {
                continue;
            }
            adjustFrequency(user, reminder);
            if (shouldSendToday(reminder)) {
                String message = aiService.generateMotivationMessage();
                emailService.sendReminder(user, message);
                reminder.setLastSent(LocalDateTime.now());
            }
            reminderRepository.save(reminder);
        }
    }

    private void adjustFrequency(User user, Reminder reminder) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);
        long completedWeek = progressLogRepository.countByUserIdAndCompletedTrueAndLogDateBetween(user.getId(), start, end);

        if (completedWeek <= 2) {
            reminder.setFrequencyPerWeek(Math.min(14, reminder.getFrequencyPerWeek() + 1));
            return;
        }
        if (completedWeek >= 5) {
            reminder.setFrequencyPerWeek(Math.max(1, reminder.getFrequencyPerWeek() - 1));
        }
    }

    private boolean shouldSendToday(Reminder reminder) {
        int everyNDays = Math.max(1, 7 / reminder.getFrequencyPerWeek());
        if (reminder.getLastSent() == null) {
            return true;
        }
        return reminder.getLastSent().toLocalDate().plusDays(everyNDays).isBefore(LocalDate.now().plusDays(1));
    }
}
