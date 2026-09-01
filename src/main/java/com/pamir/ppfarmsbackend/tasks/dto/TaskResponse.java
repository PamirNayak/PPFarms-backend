package com.pamir.ppfarmsbackend.tasks.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private UUID id;
    private UUID organizationId;
    private String title;
    private String description;
    private UUID assignedToUserId;
    private String assignedToUserName;
    private UUID shedPenId;
    private String shedPenName;
    private UUID animalId;
    private String animalTagNumber;
    private String taskType;
    private String priority;
    private String status;
    private LocalDate dueDate;
    private OffsetDateTime completedAt;
    private String notes;
    private OffsetDateTime createdAt;
}
