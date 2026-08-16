package com.pamir.ppfarmsbackend.reproduction.controller;

import com.pamir.ppfarmsbackend.reproduction.dto.BirthRequest;
import com.pamir.ppfarmsbackend.reproduction.dto.BirthRecordResponse;
import com.pamir.ppfarmsbackend.reproduction.service.ReproductionService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/births")
@RequiredArgsConstructor
@Tag(name = "Birth Delivery & Offspring Registration", description = "Endpoints for recording births and auto-generating new animal records")
@SecurityRequirement(name = "Bearer Authentication")
public class BirthController {

    private final ReproductionService reproductionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Record Birth Delivery", description = "Logs birth delivery details and automatically registers each alive kid/calf as a new animal record linked to mother and father")
    public ResponseEntity<ApiResponse<BirthRecordResponse>> recordBirth(@RequestBody @Valid BirthRequest request,
                                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        BirthRecordResponse birthRecord = reproductionService.recordBirth(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Birth delivery recorded and " + birthRecord.getAliveCount() + " offspring registered in herd", birthRecord));
    }
}