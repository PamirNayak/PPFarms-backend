package com.pamir.ppfarmsbackend.herd.service;

import com.pamir.ppfarmsbackend.herd.dto.ShedRequest;
import com.pamir.ppfarmsbackend.herd.entity.ShedPen;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface ShedService {
    ShedPen createShed(ShedRequest request, CustomUserDetails userDetails);
    List<ShedPen> getSheds(CustomUserDetails userDetails);
    ShedPen updateShed(UUID id, ShedRequest request, CustomUserDetails userDetails);
    void deleteShed(UUID id, CustomUserDetails userDetails);
}
