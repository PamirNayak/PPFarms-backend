package com.pamir.ppfarmsbackend.tasks.repository;

import com.pamir.ppfarmsbackend.tasks.domain.TaskStatus;
import com.pamir.ppfarmsbackend.tasks.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    List<Task> findByOrganizationIdAndDeletedAtIsNullOrderByDueDateAsc(UUID organizationId);

    List<Task> findByOrganizationIdAndAssignedToIdAndDeletedAtIsNullOrderByDueDateAsc(UUID organizationId, UUID userId);

    List<Task> findByOrganizationIdAndStatusAndDeletedAtIsNullOrderByDueDateAsc(UUID organizationId, TaskStatus status);

    List<Task> findByOrganizationIdAndDueDateBetweenAndDeletedAtIsNull(UUID organizationId, LocalDate start, LocalDate end);

    long countByOrganizationIdAndStatusAndDeletedAtIsNull(UUID organizationId, TaskStatus status);
}
