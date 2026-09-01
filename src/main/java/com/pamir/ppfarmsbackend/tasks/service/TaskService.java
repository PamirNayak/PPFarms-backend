package com.pamir.ppfarmsbackend.tasks.service;

import com.pamir.ppfarmsbackend.tasks.dto.TaskRequest;
import com.pamir.ppfarmsbackend.tasks.dto.TaskResponse;
import com.pamir.ppfarmsbackend.tasks.dto.TaskStatusUpdateRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(TaskRequest request, UUID tenantId);
    TaskResponse updateTask(UUID taskId, TaskRequest request, UUID tenantId);
    TaskResponse updateTaskStatus(UUID taskId, TaskStatusUpdateRequest request, UUID tenantId);
    TaskResponse getTaskById(UUID taskId, UUID tenantId);
    List<TaskResponse> getAllTasks(UUID tenantId, String status, LocalDate startDate, LocalDate endDate);
    List<TaskResponse> getMyTasks(UUID userId, UUID tenantId);
    void deleteTask(UUID taskId, UUID tenantId);
}
