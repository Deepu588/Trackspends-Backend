package com.personal.financialvault.controller;

import com.personal.financialvault.dto.request.*;
import com.personal.financialvault.dto.response.ApiResponse;
import com.personal.financialvault.dto.response.AuthResponse;
import com.personal.financialvault.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authService.refreshToken(request.get("refreshToken")));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity.ok(authService.logout(userDetails.getUsername()));
    }

    @PatchMapping("/updateSalary")
    public ResponseEntity<ApiResponse<?>> updateSalary(@AuthenticationPrincipal UserDetails userDetails,
                                                       @Valid @RequestBody UpdateSalaryRequest request
                                                       ){
            return ResponseEntity.status(200).body(authService.updateSalaryOfUser(request,userDetails.getUsername()));
    }



    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam("token") String token ){
        return ResponseEntity.status(200).body(authService.verifyEmail(token));
    }

}