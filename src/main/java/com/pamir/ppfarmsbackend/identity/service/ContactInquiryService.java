package com.pamir.ppfarmsbackend.identity.service;

import com.pamir.ppfarmsbackend.identity.dto.ContactInquiryRequest;
import com.pamir.ppfarmsbackend.identity.dto.ContactInquiryResponse;

import java.util.List;
import java.util.UUID;

public interface ContactInquiryService {
    ContactInquiryResponse submitInquiry(ContactInquiryRequest inquiry);
    List<ContactInquiryResponse> getInquiries();
    ContactInquiryResponse updateInquiryStatus(UUID id, String status);
}