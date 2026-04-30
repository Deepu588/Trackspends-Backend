package com.personal.financialvault.service;


import com.personal.financialvault.dto.request.*;
import com.personal.financialvault.dto.response.ApiResponse;
import com.personal.financialvault.dto.response.AuthResponse;
import com.personal.financialvault.entity.*;
import com.personal.financialvault.exceptions.ResourceNotFoundException;
import com.personal.financialvault.repository.PasswordResetTokenRepository;
import com.personal.financialvault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${password.reset.token.expiry}")
    private int tokenExpiryMinutes;

    //  REGISTER
    public ApiResponse<String> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .age(request.getAge())
                .maritalStatus(request.getMaritalStatus())
                .monthlySalary(request.getMonthlySalary())
                .employmentDomain(request.getEmploymentDomain())
                .isVerified(false)
                .build();


     User newlyCreatedUser=   userRepository.save(user);

       // emailService.sendRegisterConfirmationMail(newlyCreatedUser.getUsername(),newlyCreatedUser.getEmail());

                    String token=UUID.randomUUID().toString();
                    PasswordResetToken verificationToken= PasswordResetToken.builder()
                            .email(request.getEmail())
                            .token(token)
                            .expiry(LocalDateTime.now().plusHours(24))
                            .build();

                    tokenRepository.save(verificationToken);

                    emailService.sendVerificationEmail(request.getEmail(),token);
        return ApiResponse.<String> builder()
                .success(true)
                .message("Registration is successful. please verify your email within 24 hours")
                .build();
    }

    //  LOGIN
    public ApiResponse<AuthResponse> login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
                   if(! user.getIsVerified()){
                       throw new IllegalArgumentException("Email Verification not done. please verify your email");

                   }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));
        }catch(BadCredentialsException e){
            throw new BadCredentialsException("Invalid Credentials");
        }



        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);


        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                       // .message("Login Successful !!")
                        .userId(user.getUserId())
                        .userName(user.getUsername())
                        .monthlySalary(user.getMonthlySalary())
                        .build())
                .build();
    }

    public ApiResponse<AuthResponse> refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        String email = jwtService.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new IllegalArgumentException("Refresh token mismatch");
        }

        String newAccessToken = jwtService.generateAccessToken(email);

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Token refreshed")
                .data(AuthResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .userId(user.getUserId())
                        .userName(user.getUsername())
                        .monthlySalary(user.getMonthlySalary())
                        .build())
                .build();
    }

    @Transactional
    public ApiResponse<?> forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("No account found with this email"));

        // Delete old token if exists
        tokenRepository.findByEmail(request.getEmail())
                .ifPresent(t -> tokenRepository.delete(t));

        // Generate new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(request.getEmail())
                .token(token)
                .expiry(LocalDateTime.now().plusMinutes(tokenExpiryMinutes))
                .build();

        tokenRepository.save(resetToken);

        // Send email
        emailService.sendPasswordResetEmail(request.getEmail(), token);

        return ApiResponse.builder()
                .success(true)
                .message("Password reset link sent to your email")
                .build();
    }

    //  RESET PASSWORD
    @Transactional
    public ApiResponse<?> resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invalid or expired reset token"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Reset token has expired. Please request again.");
        }

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.delete(resetToken);

        return ApiResponse.builder()
                .success(true)
                .message("Password reset successful. Please login.")
                .build();
    }


    @Transactional
    public ApiResponse<String> verifyEmail(String token) {

        java.util.Optional<PasswordResetToken> verificationTokenOpt = tokenRepository.findByToken(token);

        // Token not found — check if user is already verified
        if (verificationTokenOpt.isEmpty()) {
            // Try to find user by decoding token or handle gracefully
            throw new ResourceNotFoundException("Token is invalid or already used. " +
                    "If you already verified your email, please login.");
        }

        PasswordResetToken verificationToken = verificationTokenOpt.get();

        if (verificationToken.isExpired()) {
            tokenRepository.delete(verificationToken);
            throw new IllegalArgumentException("Verification Token is expired");
        }

        User user = userRepository.findByEmail(verificationToken.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));


        if (user.getIsVerified()) {
            tokenRepository.delete(verificationToken); // cleanup if token still exists
            return ApiResponse.<String>builder()
                    .success(true)
                    .message("Email is already verified. Please login.")
                    .build();
        }

        user.setIsVerified(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Email verified successfully.")
                .build();
    }


    public ApiResponse<?> logout(String email){

      User user=  userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not Found"));

        user.setRefreshToken(null);
        userRepository.save(user);

        SecurityContextHolder.clearContext();

        return  ApiResponse.builder().success(true).message("Logged out Successfully !!").build();
    }



    @Transactional
    public ApiResponse<?> updateSalaryOfUser( UpdateSalaryRequest request,String email){
       Double salary= request.getSalary();
       User user=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User Not Found"));

       // int salaryUpdateStatus=  userRepository.updateSalaryOfUser(salary, user.getUserId());
                user.setMonthlySalary(salary);
                userRepository.save(user);
     HashMap<String,Double> map=   new HashMap<>();

        map.put("salary", user.getMonthlySalary());

        return ApiResponse
                .builder()
                .success(true)
                .message("Salary Updated Successfully !!")
                .data(map)
                .build();

    }
}