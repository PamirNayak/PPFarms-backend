package com.pamir.ppfarmsbackend.identity.service;

import com.pamir.ppfarmsbackend.identity.dto.*;

public interface AuthService {
    TokenResponse registerFarm(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    TokenResponse refreshToken(RefreshTokenRequest request);
    void sendOtp(SendOtpRequest request);
    boolean verifyOtp(VerifyOtpRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}

