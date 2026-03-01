package com.alibou.stockmanage.auths.web.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
     @NotBlank
     @Email
     String email
) {
}
