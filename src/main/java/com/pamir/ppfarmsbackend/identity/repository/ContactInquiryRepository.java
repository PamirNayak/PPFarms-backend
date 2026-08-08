package com.pamir.ppfarmsbackend.identity.repository;

import com.pamir.ppfarmsbackend.identity.entity.ContactInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactInquiryRepository extends JpaRepository<ContactInquiry, UUID> {
    List<ContactInquiry> findByOrderByCreatedAtDesc();
}
