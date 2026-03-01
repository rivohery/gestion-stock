package com.alibou.stockmanage.auths.web.dtos;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull(message = "User ID is not null")
        Long id,
        @NotNull
        boolean enabled
) {
}
