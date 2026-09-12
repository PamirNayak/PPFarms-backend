package com.pamir.ppfarmsbackend.shared.realtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseEmitterService {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterService.class);
    private static final Long EMITTER_TIMEOUT = 30 * 60 * 1000L; // 30 minutes

    public record ClientConnection(UUID userId, UUID organizationId, String role, SseEmitter emitter) {}

    private final CopyOnWriteArrayList<ClientConnection> connections = new CopyOnWriteArrayList<>();

    public SseEmitter registerClient(UUID userId, UUID organizationId, String role) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT);
        ClientConnection connection = new ClientConnection(userId, organizationId, role != null ? role.toUpperCase() : "WORKER", emitter);

        connections.add(connection);
        log.info("[SSE] Client connected: user={}, org={}, role={}. Total active: {}", userId, organizationId, role, connections.size());

        emitter.onCompletion(() -> {
            connections.remove(connection);
            log.info("[SSE] Client completed: user={}. Total active: {}", userId, connections.size());
        });

        emitter.onTimeout(() -> {
            connections.remove(connection);
            emitter.complete();
            log.info("[SSE] Client timed out: user={}. Total active: {}", userId, connections.size());
        });

        emitter.onError(e -> {
            connections.remove(connection);
            log.debug("[SSE] Client connection error: user={}, msg={}", userId, e.getMessage());
        });

        // Send initial connected confirmation ping
        try {
            emitter.send(SseEmitter.event()
                    .name("CONNECTED")
                    .data(Map.of("message", "Real-time sync connected", "userId", userId.toString())));
        } catch (IOException e) {
            connections.remove(connection);
        }

        return emitter;
    }

    public void sendToUser(UUID userId, String eventName, Object data) {
        if (userId == null) return;
        for (ClientConnection conn : connections) {
            if (userId.equals(conn.userId())) {
                sendEvent(conn, eventName, data);
            }
        }
    }

    public void sendToOrganization(UUID organizationId, String eventName, Object data) {
        if (organizationId == null) return;
        for (ClientConnection conn : connections) {
            if (organizationId.equals(conn.organizationId())) {
                sendEvent(conn, eventName, data);
            }
        }
    }

    public void sendToSuperAdmins(String eventName, Object data) {
        for (ClientConnection conn : connections) {
            if ("SUPER_ADMIN".equalsIgnoreCase(conn.role())) {
                sendEvent(conn, eventName, data);
            }
        }
    }

    public void broadcast(String eventName, Object data) {
        for (ClientConnection conn : connections) {
            sendEvent(conn, eventName, data);
        }
    }

    private void sendEvent(ClientConnection conn, String eventName, Object data) {
        try {
            conn.emitter().send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
        } catch (Exception e) {
            connections.remove(conn);
        }
    }

    @Scheduled(fixedRate = 25000)
    public void sendHeartbeat() {
        for (ClientConnection conn : connections) {
            try {
                conn.emitter().send(SseEmitter.event()
                        .name("HEARTBEAT")
                        .data(Map.of("timestamp", System.currentTimeMillis())));
            } catch (Exception e) {
                connections.remove(conn);
            }
        }
    }
}