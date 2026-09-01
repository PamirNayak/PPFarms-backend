package com.pamir.ppfarmsbackend.tasks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    private UUID assignedToUserId;
    private UUID shedPenId;
    private UUID animalId;

    @Builder.Default
    private String taskType = "GENERAL"; // FEEDING, VACCINATION, CLEANING, WEIGHING, CHECKUP, GENERAL

    @Builder.Default
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private String notes;
}
