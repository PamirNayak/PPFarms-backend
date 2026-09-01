package com.pamir.ppfarmsbackend.tasks.service.impl;

import com.pamir.ppfarmsbackend.herd.entity.Animal;
import com.pamir.ppfarmsbackend.herd.entity.ShedPen;
import com.pamir.ppfarmsbackend.herd.repository.AnimalRepository;
import com.pamir.ppfarmsbackend.herd.repository.ShedPenRepository;
import com.pamir.ppfarmsbackend.identity.entity.User;
import com.pamir.ppfarmsbackend.identity.repository.UserRepository;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import com.pamir.ppfarmsbackend.tasks.domain.TaskPriority;
import com.pamir.ppfarmsbackend.tasks.domain.TaskStatus;
import com.pamir.ppfarmsbackend.tasks.dto.TaskRequest;
import com.pamir.ppfarmsbackend.tasks.dto.TaskResponse;
import com.pamir.ppfarmsbackend.tasks.dto.TaskStatusUpdateRequest;
import com.pamir.ppfarmsbackend.tasks.entity.Task;
import com.pamir.ppfarmsbackend.tasks.repository.TaskRepository;
import com.pamir.ppfarmsbackend.tasks.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ShedPenRepository shedPenRepository;
    private final AnimalRepository animalRepository;

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request, UUID tenantId) {
        User assignedTo = null;
        if (request.getAssignedToUserId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff user not found"));
        }

        ShedPen shedPen = null;
        if (request.getShedPenId() != null) {
            shedPen = shedPenRepository.findById(request.getShedPenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shed/Pen not found"));
        }

        Animal animal = null;
        if (request.getAnimalId() != null) {
            animal = animalRepository.findById(request.getAnimalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
        }

        TaskPriority priority = parsePriority(request.getPriority());

        Task task = Task.builder()
                .organizationId(tenantId)
                .title(request.getTitle())
                .description(request.getDescription())
                .assignedTo(assignedTo)
                .shedPen(shedPen)
                .animal(animal)
                .taskType(request.getTaskType() != null ? request.getTaskType().toUpperCase() : "GENERAL")
                .priority(priority)
                .status(TaskStatus.PENDING)
                .dueDate(request.getDueDate())
                .notes(request.getNotes())
                .build();

        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(UUID taskId, TaskRequest request, UUID tenantId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        if (request.getAssignedToUserId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff user not found"));
            task.setAssignedTo(assignedTo);
        } else {
            task.setAssignedTo(null);
        }

        if (request.getShedPenId() != null) {
            ShedPen shedPen = shedPenRepository.findById(request.getShedPenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shed/Pen not found"));
            task.setShedPen(shedPen);
        } else {
            task.setShedPen(null);
        }

        if (request.getAnimalId() != null) {
            Animal animal = animalRepository.findById(request.getAnimalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
            task.setAnimal(animal);
        } else {
            task.setAnimal(null);
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setTaskType(request.getTaskType() != null ? request.getTaskType().toUpperCase() : task.getTaskType());
        task.setPriority(parsePriority(request.getPriority()));
        task.setDueDate(request.getDueDate());
        task.setNotes(request.getNotes());

        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatus(UUID taskId, TaskStatusUpdateRequest request, UUID tenantId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        TaskStatus status = parseStatus(request.getStatus());
        task.setStatus(status);

        if (status == TaskStatus.COMPLETED) {
            task.setCompletedAt(OffsetDateTime.now());
        } else {
            task.setCompletedAt(null);
        }

        if (request.getNotes() != null) {
            task.setNotes(request.getNotes());
        }

        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID taskId, UUID tenantId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        return mapToResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks(UUID tenantId, String statusStr, LocalDate startDate, LocalDate endDate) {
        List<Task> tasks;

        if (statusStr != null && !statusStr.isBlank()) {
            TaskStatus status = parseStatus(statusStr);
            tasks = taskRepository.findByOrganizationIdAndStatusAndDeletedAtIsNullOrderByDueDateAsc(tenantId, status);
        } else if (startDate != null && endDate != null) {
            tasks = taskRepository.findByOrganizationIdAndDueDateBetweenAndDeletedAtIsNull(tenantId, startDate, endDate);
        } else {
            tasks = taskRepository.findByOrganizationIdAndDeletedAtIsNullOrderByDueDateAsc(tenantId);
        }

        return tasks.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasks(UUID userId, UUID tenantId) {
        return taskRepository.findByOrganizationIdAndAssignedToIdAndDeletedAtIsNullOrderByDueDateAsc(tenantId, userId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public void deleteTask(UUID taskId, UUID tenantId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        taskRepository.delete(task);
    }

    private TaskPriority parsePriority(String prioStr) {
        if (prioStr == null) return TaskPriority.MEDIUM;
        try {
            return TaskPriority.valueOf(prioStr.trim().toUpperCase());
        } catch (Exception e) {
            return TaskPriority.MEDIUM;
        }
    }

    private TaskStatus parseStatus(String statusStr) {
        if (statusStr == null) return TaskStatus.PENDING;
        try {
            return TaskStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (Exception e) {
            return TaskStatus.PENDING;
        }
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .organizationId(task.getOrganizationId())
                .title(task.getTitle())
                .description(task.getDescription())
                .assignedToUserId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null)
                .assignedToUserName(task.getAssignedTo() != null ? task.getAssignedTo().getName() : null)
                .shedPenId(task.getShedPen() != null ? task.getShedPen().getId() : null)
                .shedPenName(task.getShedPen() != null ? task.getShedPen().getName() : null)
                .animalId(task.getAnimal() != null ? task.getAnimal().getId() : null)
                .animalTagNumber(task.getAnimal() != null ? task.getAnimal().getTagNumber() : null)
                .taskType(task.getTaskType())
                .priority(task.getPriority().name())
                .status(task.getStatus().name())
                .dueDate(task.getDueDate())
                .completedAt(task.getCompletedAt())
                .notes(task.getNotes())
                .createdAt(task.getCreatedAt())
                .build();
    }
}
