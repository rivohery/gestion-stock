package com.alibou.stockmanage.auths.web.dtos;

import jakarta.validation.constraints.NotBlank;

public record OTPTokenRequest(
        @NotBlank(message = "OTP Token is required")
        String token
) {
}
