package com.pamir.ppfarmsbackend.identity.service.impl;

import com.pamir.ppfarmsbackend.identity.dto.ContactInquiryRequest;
import com.pamir.ppfarmsbackend.identity.dto.ContactInquiryResponse;
import com.pamir.ppfarmsbackend.identity.entity.ContactInquiry;
import com.pamir.ppfarmsbackend.identity.repository.ContactInquiryRepository;
import com.pamir.ppfarmsbackend.identity.service.ContactInquiryService;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ContactInquiryServiceImpl implements ContactInquiryService {

    private final ContactInquiryRepository inquiryRepository;

    @Override
    @Transactional
    public ContactInquiryResponse submitInquiry(ContactInquiryRequest request) {
        ContactInquiry inquiry = ContactInquiry.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone() != null ? request.getPhone() : "")
                .farmType(request.getFarmType() != null ? request.getFarmType() : "DAIRY")
                .estimatedHerdSize(request.getEstimatedHerdSize())
                .message(request.getMessage())
                .status("NEW")
                .build();

        return ContactInquiryResponse.fromEntity(inquiryRepository.save(inquiry));
    }

    @Override
    public List<ContactInquiryResponse> getInquiries() {
        return inquiryRepository.findByOrderByCreatedAtDesc()
                .stream()
                .map(ContactInquiryResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public ContactInquiryResponse updateInquiryStatus(UUID id, String status) {
        ContactInquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found"));
        inquiry.setStatus(status);
        return ContactInquiryResponse.fromEntity(inquiryRepository.save(inquiry));
    }
}