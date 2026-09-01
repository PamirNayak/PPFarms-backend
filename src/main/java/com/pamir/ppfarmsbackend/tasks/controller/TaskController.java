package com.pamir.ppfarmsbackend.tasks.controller;

import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import com.pamir.ppfarmsbackend.tasks.dto.TaskRequest;
import com.pamir.ppfarmsbackend.tasks.dto.TaskResponse;
import com.pamir.ppfarmsbackend.tasks.dto.TaskStatusUpdateRequest;
import com.pamir.ppfarmsbackend.tasks.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Daily Farm Task Management", description = "Endpoints for assigning and completing daily farm staff tasks (feeding, vaccination, cleaning, weighing)")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Create Farm Task", description = "Assigns a new operational task to a staff worker or shed")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@RequestBody @Valid TaskRequest request,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        TaskResponse response = taskService.createTask(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "List All Farm Tasks", description = "Retrieves all farm tasks with optional status or date range filter")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TaskResponse> tasks = taskService.getAllTasks(userDetails.getTenantId(), status, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/my-tasks")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Tasks Assigned to Me", description = "Retrieves pending tasks assigned to the currently logged-in worker")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getMyTasks(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TaskResponse> tasks = taskService.getMyTasks(userDetails.getId(), userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Task by ID", description = "Retrieves details of a specific task")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(@PathVariable UUID id,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        TaskResponse response = taskService.getTaskById(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Update Task Details", description = "Updates title, assignee, shed, or due date for a task")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(@PathVariable UUID id,
                                                                 @RequestBody @Valid TaskRequest request,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        TaskResponse response = taskService.updateTask(id, request, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", response));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Update Task Status / Complete Task", description = "Updates status to IN_PROGRESS, COMPLETED, or CANCELLED")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(@PathVariable UUID id,
                                                                       @RequestBody @Valid TaskStatusUpdateRequest request,
                                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        TaskResponse response = taskService.updateTaskStatus(id, request, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Task status updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Delete Task", description = "Deletes a task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable UUID id,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        taskService.deleteTask(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }
}
