package com.pamir.ppfarmsbackend.tasks.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED

    private String notes;
}
