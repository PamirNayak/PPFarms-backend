package com.pamir.ppfarmsbackend.identity.controller;

import com.pamir.ppfarmsbackend.identity.dto.*;
import com.pamir.ppfarmsbackend.identity.service.AuthService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.security.ratelimit.RateLimit;
import com.pamir.ppfarmsbackend.shared.security.ratelimit.RateLimitKeyType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication & Onboarding", description = "Endpoints for farm self-registration, login, OTP, and JWT refresh")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-otp")
    @RateLimit(limit = 5, windowSeconds = 60, keyType = RateLimitKeyType.IP)
    @Operation(summary = "Send OTP", description = "Sends SHA-256 hashed 6-digit OTP code to user email")
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestBody @Valid SendOtpRequest request) {
        authService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully to " + request.getEmail(), null));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP", description = "Validates 6-digit OTP code with single-use burn")
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(@RequestBody @Valid VerifyOtpRequest request) {
        boolean valid = authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", valid));
    }

    @PostMapping("/forgot-password")
    @RateLimit(limit = 5, windowSeconds = 60, keyType = RateLimitKeyType.IP)
    @Operation(summary = "Forgot Password Request", description = "Sends password reset OTP to user email")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset OTP sent to " + request.getEmail(), null));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Resets password using verified OTP")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully. You can now login.", null));
    }

    @PostMapping("/register")
    @RateLimit(limit = 10, windowSeconds = 60, keyType = RateLimitKeyType.IP)
    @Operation(summary = "Register New Farm & Admin Account", description = "Self-service onboarding endpoint creating an Organization and Owner ADMIN user")
    public ResponseEntity<ApiResponse<TokenResponse>> register(@RequestBody @Valid RegisterRequest request) {
        TokenResponse tokenResponse = authService.registerFarm(request);

        org.springframework.http.ResponseCookie accessCookie = org.springframework.http.ResponseCookie.from("access_token", tokenResponse.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        org.springframework.http.ResponseCookie refreshCookie = org.springframework.http.ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        tokenResponse.setRefreshToken(null);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.success("Farm and Owner registered successfully", tokenResponse));
    }

    @PostMapping("/login")
    @RateLimit(limit = 10, windowSeconds = 60, keyType = RateLimitKeyType.IP)
    @Operation(summary = "User Login", description = "Authenticates user credentials, sets HttpOnly cookies for access_token and refreshToken")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);

        org.springframework.http.ResponseCookie accessCookie = org.springframework.http.ResponseCookie.from("access_token", tokenResponse.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        org.springframework.http.ResponseCookie refreshCookie = org.springframework.http.ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        tokenResponse.setRefreshToken(null);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.success("Login successful", tokenResponse));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh Access Token", description = "Exchanges a valid refresh token for new access_token and refreshToken HttpOnly cookies")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String cookieRefreshToken,
            @RequestBody(required = false) RefreshTokenRequest request) {

        String tokenToUse = (cookieRefreshToken != null && !cookieRefreshToken.isBlank())
                ? cookieRefreshToken
                : (request != null ? request.getRefreshToken() : null);

        if (tokenToUse == null || tokenToUse.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Refresh token missing from both HttpOnly cookie and request body"));
        }

        RefreshTokenRequest refreshReq = new RefreshTokenRequest();
        refreshReq.setRefreshToken(tokenToUse);

        TokenResponse tokenResponse = authService.refreshToken(refreshReq);

        org.springframework.http.ResponseCookie accessCookie = org.springframework.http.ResponseCookie.from("access_token", tokenResponse.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        org.springframework.http.ResponseCookie refreshCookie = org.springframework.http.ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        tokenResponse.setRefreshToken(null);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.success("Token refreshed successfully", tokenResponse));
    }

    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Clears HttpOnly access_token and refreshToken cookies")
    public ResponseEntity<ApiResponse<String>> logout() {
        org.springframework.http.ResponseCookie accessCookie = org.springframework.http.ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        org.springframework.http.ResponseCookie refreshCookie = org.springframework.http.ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.success("Logout successful. HttpOnly cookies cleared.", null));
    }
}


