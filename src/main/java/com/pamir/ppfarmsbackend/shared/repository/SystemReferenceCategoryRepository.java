package com.pamir.ppfarmsbackend.shared.repository;

import com.pamir.ppfarmsbackend.shared.entity.SystemReferenceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SystemReferenceCategoryRepository extends JpaRepository<SystemReferenceCategory, UUID> {
    List<SystemReferenceCategory> findByIsActiveTrueOrderBySortOrderAsc();
    List<SystemReferenceCategory> findByCategoryTypeAndIsActiveTrueOrderBySortOrderAsc(String categoryType);
}
