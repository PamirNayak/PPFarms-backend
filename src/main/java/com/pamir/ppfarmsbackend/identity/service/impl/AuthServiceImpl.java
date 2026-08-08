package com.pamir.ppfarmsbackend.identity.service.impl;

import com.pamir.ppfarmsbackend.billing.entity.Plan;
import com.pamir.ppfarmsbackend.billing.entity.Subscription;
import com.pamir.ppfarmsbackend.billing.repository.PlanRepository;
import com.pamir.ppfarmsbackend.billing.repository.SubscriptionRepository;
import com.pamir.ppfarmsbackend.identity.dto.*;
import com.pamir.ppfarmsbackend.identity.entity.Organization;
import com.pamir.ppfarmsbackend.identity.entity.RefreshToken;
import com.pamir.ppfarmsbackend.identity.entity.Role;
import com.pamir.ppfarmsbackend.identity.entity.User;
import com.pamir.ppfarmsbackend.identity.repository.OrganizationRepository;
import com.pamir.ppfarmsbackend.identity.repository.RefreshTokenRepository;
import com.pamir.ppfarmsbackend.identity.repository.RoleRepository;
import com.pamir.ppfarmsbackend.identity.repository.UserRepository;
import com.pamir.ppfarmsbackend.identity.service.AuthService;
import com.pamir.ppfarmsbackend.shared.email.service.EmailService;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.InvalidOperationException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import com.pamir.ppfarmsbackend.shared.exception.UnauthorizedException;
import com.pamir.ppfarmsbackend.shared.security.JwtTokenProvider;
import com.pamir.ppfarmsbackend.identity.event.AuthEvents;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;

    private static final String OTP_PREFIX = "OTP:";
    private static final Map<String, String> inMemoryOtpStore = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public TokenResponse registerFarm(RegisterRequest request) {
        if (organizationRepository.existsByEmail(request.getEmail()) || userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Organization organization = Organization.builder()
                .name(request.getFarmName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .status("ACTIVE")
                .build();
        organization = organizationRepository.save(organization);

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("ADMIN role not configured in database"));

        User user = User.builder()
                .organizationId(organization.getId())
                .role(adminRole)
                .name(request.getOwnerName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status("ACTIVE")
                .build();
        user = userRepository.save(user);

        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), adminRole.getName(), organization.getId());
        String refreshTokenStr = tokenProvider.generateRefreshToken(user.getId());

        saveRefreshToken(user.getId(), refreshTokenStr);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .user(TokenResponse.UserSummary.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(adminRole.getName())
                        .organizationId(organization.getId())
                        .organizationName(organization.getName())
                        .build())
                .build();
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new UnauthorizedException("User account is inactive or suspended");
        }

        Organization organization = organizationRepository.findById(user.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().getName(), organization.getId());
        String refreshTokenStr = tokenProvider.generateRefreshToken(user.getId());

        saveRefreshToken(user.getId(), refreshTokenStr);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .user(TokenResponse.UserSummary.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole().getName())
                        .organizationId(organization.getId())
                        .organizationName(organization.getName())
                        .build())
                .build();
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found or revoked"));

        if (refreshToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token has expired");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Organization organization = organizationRepository.findById(user.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        String newAccessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().getName(), organization.getId());
        String newRefreshTokenStr = tokenProvider.generateRefreshToken(user.getId());

        refreshTokenRepository.delete(refreshToken);
        saveRefreshToken(user.getId(), newRefreshTokenStr);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenStr)
                .user(TokenResponse.UserSummary.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole().getName())
                        .organizationId(organization.getId())
                        .organizationName(organization.getName())
                        .build())
                .build();
    }

    private static volatile boolean isRedisAvailable = true;
    private static volatile long lastRedisFailureTime = 0;

    private boolean checkRedisAvailable() {
        if (redisTemplate == null) return false;
        if (!isRedisAvailable) {
            // Skip Redis check for 10 minutes if offline to keep API responses ultra-fast (< 5ms)
            if (System.currentTimeMillis() - lastRedisFailureTime < 600000) {
                return false;
            }
            isRedisAvailable = true;
        }
        return true;
    }

    @Override
    public void sendOtp(SendOtpRequest request) {
        String rawOtp = String.format("%06d", new SecureRandom().nextInt(900000) + 100000);
        String hashedOtp = hashOtp(request.getEmail(), rawOtp);
        String redisKey = OTP_PREFIX + request.getEmail().toLowerCase();

        boolean storedInRedis = false;
        if (checkRedisAvailable()) {
            try {
                redisTemplate.opsForValue().set(redisKey, hashedOtp, Duration.ofMinutes(5));
                storedInRedis = true;
            } catch (Exception e) {
                isRedisAvailable = false;
                lastRedisFailureTime = System.currentTimeMillis();
                log.warn("Redis unavailable for OTP set ({}). Fast fallback to in-memory store.", e.getMessage());
            }
        }

        if (!storedInRedis) {
            inMemoryOtpStore.put(redisKey, hashedOtp);
        }

        log.info("[OTP GENERATED] Target: {}", request.getEmail());

        // Publish decoupled Java 21 domain event
        eventPublisher.publishEvent(new AuthEvents.OtpRequestedEvent(
                request.getEmail(),
                rawOtp,
                "otp-email"
        ));
    }

    @Override
    public boolean verifyOtp(VerifyOtpRequest request) {
        String redisKey = OTP_PREFIX + request.getEmail().toLowerCase();
        String expectedHash = null;

        if (checkRedisAvailable()) {
            try {
                expectedHash = redisTemplate.opsForValue().get(redisKey);
            } catch (Exception e) {
                isRedisAvailable = false;
                lastRedisFailureTime = System.currentTimeMillis();
                log.warn("Redis unavailable for OTP get. Fast fallback to in-memory store.");
            }
        }

        if (expectedHash == null) {
            expectedHash = inMemoryOtpStore.get(redisKey);
        }

        if (expectedHash == null) {
            throw new BadRequestException("OTP expired or not found. Please request a new OTP.");
        }

        String inputHash = hashOtp(request.getEmail(), request.getOtp());
        if (!expectedHash.equalsIgnoreCase(inputHash)) {
            throw new BadRequestException("Invalid OTP entered. Please try again.");
        }

        // Single-use burn
        if (checkRedisAvailable()) {
            try {
                redisTemplate.delete(redisKey);
            } catch (Exception ignored) {}
        }
        inMemoryOtpStore.remove(redisKey);

        return true;
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No user found with email " + request.getEmail()));

        SendOtpRequest sendReq = new SendOtpRequest();
        sendReq.setEmail(user.getEmail());
        sendOtp(sendReq);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        VerifyOtpRequest verifyReq = new VerifyOtpRequest();
        verifyReq.setEmail(request.getEmail());
        verifyReq.setOtp(request.getOtp());
        verifyOtp(verifyReq);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Publish decoupled Java 21 domain event
        eventPublisher.publishEvent(new AuthEvents.PasswordResetSuccessEvent(
                user.getEmail(),
                user.getName()
        ));
    }

    private String hashOtp(String email, String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = email.toLowerCase() + ":" + otp + ":PPFARMS_SALT_2026";
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Error hashing OTP for email {}", email, e);
            throw new InvalidOperationException("Failed to generate secure OTP token. Please try again.");
        }
    }

    private void saveRefreshToken(UUID userId, String tokenStr) {
        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .tokenHash(tokenStr)
                .expiresAt(OffsetDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(token);
    }
}

