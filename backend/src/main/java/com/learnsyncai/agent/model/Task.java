package com.learnsyncai.agent.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private StudyPlan studyPlan;

    @Column(nullable = false)
    private Integer dayNumber;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, length = 300)
    private String revisionNote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.PENDING;
}
