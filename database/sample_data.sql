USE learnsyncai;

-- password hash corresponds to: Password@123
INSERT INTO users (id, name, email, password, created_at)
VALUES
    (1, 'Demo Student', 'demo@learnsync.ai', '$2a$10$p6lTRXcdDUzP/FQ3fY6VfOzGi99ZTSdKCbFf8gDuwZQzQ0imeISF6', NOW())
ON DUPLICATE KEY UPDATE email = email;

INSERT INTO study_plans (id, user_id, goal, duration_days, difficulty, daily_hours, created_at)
VALUES
    (1, 1, 'Learn Web Development in 30 days', 30, 'BEGINNER', 2, NOW())
ON DUPLICATE KEY UPDATE goal = goal;

INSERT INTO tasks (id, plan_id, day_number, description, revision_note, status)
VALUES
    (1, 1, 1, 'Understand HTML structure and semantic tags', 'Revise tags with a quick HTML skeleton', 'COMPLETED'),
    (2, 1, 2, 'Practice CSS selectors, box model, and layouts', 'Review with mini styling challenge', 'COMPLETED'),
    (3, 1, 3, 'Build a responsive landing page', 'Write key takeaways and rework sections', 'PENDING')
ON DUPLICATE KEY UPDATE description = description;

INSERT INTO progress_logs (id, user_id, task_id, completed, log_date)
VALUES
    (1, 1, 1, TRUE, CURDATE() - INTERVAL 2 DAY),
    (2, 1, 2, TRUE, CURDATE() - INTERVAL 1 DAY),
    (3, 1, 3, FALSE, CURDATE())
ON DUPLICATE KEY UPDATE completed = completed;

INSERT INTO reminders (id, user_id, frequency_per_week, last_sent)
VALUES
    (1, 1, 3, NOW() - INTERVAL 1 DAY)
ON DUPLICATE KEY UPDATE frequency_per_week = VALUES(frequency_per_week);
