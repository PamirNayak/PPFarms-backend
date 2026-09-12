package com.pamir.ppfarmsbackend.identity.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InquiryStatus {
    NEW("New Inquiry"),
    IN_PROGRESS("In Progress"),
    CONTACTED("Contacted"),
    CLOSED("Closed");

    private final String displayName;
}
