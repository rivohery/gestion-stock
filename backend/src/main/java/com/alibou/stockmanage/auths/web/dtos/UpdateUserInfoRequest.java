package com.alibou.stockmanage.auths.web.dtos;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

public record UpdateUserInfoRequest(
       @NotNull(message = "User Id is required")
       Long userId,
       @Nullable
       String email,
       @Nullable
       String firstName,
       @Nullable
       String lastName,
       @Nullable
       String phoneNu
) {
}
