package com.pamir.ppfarmsbackend.herd.service.impl;

import com.pamir.ppfarmsbackend.flock.repository.FlockBatchRepository;
import com.pamir.ppfarmsbackend.herd.dto.ShedRequest;
import com.pamir.ppfarmsbackend.herd.entity.ShedPen;
import com.pamir.ppfarmsbackend.herd.repository.AnimalRepository;
import com.pamir.ppfarmsbackend.herd.repository.ShedPenRepository;
import com.pamir.ppfarmsbackend.herd.service.ShedService;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShedServiceImpl implements ShedService {

    private final ShedPenRepository shedPenRepository;
    private final AnimalRepository animalRepository;
    private final FlockBatchRepository flockBatchRepository;

    @Override
    @Transactional
    public ShedPen createShed(ShedRequest request, CustomUserDetails userDetails) {
        log.info("[SHED CREATE] Tenant {} creating shed/pen '{}'", userDetails.getTenantId(), request.getName());
        ShedPen shedPen = ShedPen.builder()
                .organizationId(userDetails.getTenantId())
                .name(request.getName().trim())
                .penType(request.getPenType())
                .capacity(request.getCapacity())
                .build();
        return shedPenRepository.save(shedPen);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShedPen> getSheds(CustomUserDetails userDetails) {
        return shedPenRepository.findByOrganizationIdAndDeletedAtIsNull(userDetails.getTenantId());
    }

    @Override
    @Transactional
    public ShedPen updateShed(UUID id, ShedRequest request, CustomUserDetails userDetails) {
        ShedPen shedPen = shedPenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shed/Pen not found with id " + id));

        boolean isSuperAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        if (!isSuperAdmin && shedPen.getOrganizationId() != null && !shedPen.getOrganizationId().equals(userDetails.getTenantId())) {
            throw new BadRequestException("Access denied: You can only modify sheds belonging to your farm.");
        }

        shedPen.setName(request.getName().trim());
        shedPen.setPenType(request.getPenType());
        shedPen.setCapacity(request.getCapacity());
        return shedPenRepository.save(shedPen);
    }

    @Override
    @Transactional
    public void deleteShed(UUID id, CustomUserDetails userDetails) {
        ShedPen shedPen = shedPenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shed/Pen not found with id " + id));

        boolean isSuperAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        if (!isSuperAdmin && shedPen.getOrganizationId() != null && !shedPen.getOrganizationId().equals(userDetails.getTenantId())) {
            throw new BadRequestException("Access denied: You can only delete sheds belonging to your farm.");
        }

        if (animalRepository.existsByShedPenIdAndDeletedAtIsNull(id)) {
            throw new BadRequestException("Cannot delete shed/pen '" + shedPen.getName() + "' because active animals are currently housed in it. Please relocate them first.");
        }

        if (flockBatchRepository.existsByShedPenIdAndDeletedAtIsNull(id)) {
            throw new BadRequestException("Cannot delete shed/pen '" + shedPen.getName() + "' because active poultry flock batches are currently assigned to it.");
        }

        shedPen.setDeletedAt(OffsetDateTime.now());
        shedPenRepository.save(shedPen);
        log.info("[SHED DELETE] Soft-deleted shed {} (ID: {}) for Tenant: {}", shedPen.getName(), id, userDetails.getTenantId());
    }
}
