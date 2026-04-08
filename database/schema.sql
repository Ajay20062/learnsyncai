CREATE DATABASE IF NOT EXISTS learnsyncai;
USE learnsyncai;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS study_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    goal VARCHAR(300) NOT NULL,
    duration_days INT NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    daily_hours INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_plan_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NOT NULL,
    day_number INT NOT NULL,
    description VARCHAR(500) NOT NULL,
    revision_note VARCHAR(300) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_task_plan FOREIGN KEY (plan_id) REFERENCES study_plans(id)
);

CREATE TABLE IF NOT EXISTS progress_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    completed BOOLEAN NOT NULL,
    log_date DATE NOT NULL,
    CONSTRAINT fk_progress_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_progress_task FOREIGN KEY (task_id) REFERENCES tasks(id)
);

CREATE TABLE IF NOT EXISTS reminders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    frequency_per_week INT NOT NULL DEFAULT 3,
    last_sent DATETIME NULL,
    CONSTRAINT fk_reminder_user FOREIGN KEY (user_id) REFERENCES users(id)
);
