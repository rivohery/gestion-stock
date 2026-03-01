package com.alibou.stockmanage.auths.web.dtos;

import com.alibou.stockmanage.auths.config.MsgOfValidationConstant;
import com.alibou.stockmanage.auths.models.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateUserRequest(
        @NotBlank(message = MsgOfValidationConstant.MSG_OF_FIRSTNAME_REQUIRED)
        @Size(min = 2, max = 30, message = MsgOfValidationConstant.MSG_OF_INVALID_FIRSTNAME)
        String firstName,
        @NotBlank(message = MsgOfValidationConstant.MSG_OF_LASTNAME_REQUIRED)
        @Size(min = 2, max = 30, message = MsgOfValidationConstant.MSG_OF_INVALID_LASTNAME)
        String lastName,
        @NotBlank(message = MsgOfValidationConstant.MSG_OF_PHONE_NUMBER_REQUIRED)
        String phoneNu,
        @NotBlank(message = MsgOfValidationConstant.MSG_OF_EMAIL_REQUIRED)
        @Email(message = MsgOfValidationConstant.MSG_OF_INVALID_EMAIL)
        String email,
        @NotNull
        Set<RoleEnum> roles
) {
}
