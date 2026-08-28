package com.pamir.ppfarmsbackend.sales.repository;

import com.pamir.ppfarmsbackend.sales.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {
    List<SaleItem> findBySaleId(UUID saleId);
    List<SaleItem> findByAnimalId(UUID animalId);
}
