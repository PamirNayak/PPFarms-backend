package com.pamir.ppfarmsbackend.shared.realtime;

import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/realtime")
@RequiredArgsConstructor
@Tag(name = "Real-Time Event Stream", description = "Server-Sent Events (SSE) stream for real-time reactivity without manual page refresh")
@SecurityRequirement(name = "Bearer Authentication")
public class RealTimeController {

    private final SseEmitterService sseEmitterService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'WORKER', 'MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Subscribe to Real-Time Event Stream", description = "Establishes a persistent SSE stream connection to receive live system updates")
    public SseEmitter streamEvents(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return sseEmitterService.registerClient(
                userDetails.getId(),
                userDetails.getTenantId(),
                userDetails.getRole()
        );
    }
}