package com.alibou.stockmanage.auths.web.controllers;

import com.alibou.stockmanage.auths.services.AuthService;
import com.alibou.stockmanage.auths.web.dtos.*;
import com.alibou.stockmanage.shared.dtos.GlobalResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "authentication-endpoint")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login-by-password")
    public ResponseEntity<LoginResponse> loginByPassword(
          @RequestBody @Valid LoginRequest request,
          HttpServletResponse response
    ){
        return ResponseEntity.ok(authService.loginByPassword(request, response));
    }

    @PostMapping("/forgot-password-process")
    public ResponseEntity<GlobalResponse> forgotPasswordProcess(
         @Valid @RequestBody ForgotPasswordRequest request
    ){
        authService.forgotPasswordProcess(request.email());
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .message("OTP token was sent in your address email")
                        .build()
        );
    }

    @PostMapping("/login-by-otp")
    public ResponseEntity<LoginResponse>loginByOTP(
            @Valid @RequestBody OTPTokenRequest request,
            HttpServletResponse response
    ){
        return ResponseEntity.ok(authService.loginByOTP(request.token(), response));
    }

    @PostMapping("/login-by-refresh-token")
    public void loginByRefreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authService.loginByRefreshToken(request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<GlobalResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        authService.logout(request, response);
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Logout successfully")
                        .build()
        );
    }

    @PostMapping("/update-password")
    public ResponseEntity<GlobalResponse>resetPassword(
            @RequestBody @Valid UpdatePasswordRequest request
    ){
        Long id = authService.resetPassword(request);
        if(id != null){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message("Password was changed")
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }


}
