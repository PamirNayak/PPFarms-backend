package com.pamir.ppfarmsbackend.tasks.entity;

import com.pamir.ppfarmsbackend.herd.entity.Animal;
import com.pamir.ppfarmsbackend.herd.entity.ShedPen;
import com.pamir.ppfarmsbackend.identity.entity.User;
import com.pamir.ppfarmsbackend.shared.domain.BaseEntity;
import com.pamir.ppfarmsbackend.tasks.domain.TaskPriority;
import com.pamir.ppfarmsbackend.tasks.domain.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_tasks_org_status", columnList = "organization_id, status"),
    @Index(name = "idx_tasks_org_user", columnList = "organization_id, assigned_to_user_id"),
    @Index(name = "idx_tasks_org_due", columnList = "organization_id, due_date")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shed_pen_id")
    private ShedPen shedPen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @Column(name = "task_type", nullable = false, length = 50)
    @Builder.Default
    private String taskType = "GENERAL"; // FEEDING, VACCINATION, CLEANING, WEIGHING, CHECKUP, GENERAL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private TaskStatus status = TaskStatus.PENDING;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
