package com.alibou.stockmanage.auths.web.dtos;

import com.alibou.stockmanage.auths.config.MsgOfValidationConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @NotBlank(message = MsgOfValidationConstant.MSG_OF_EMAIL_REQUIRED)
        @Email(message = MsgOfValidationConstant.MSG_OF_INVALID_EMAIL)
        String email,
        @NotBlank(message = MsgOfValidationConstant.MSG_OF_OLD_PASSWORD_REQUIRED)
        @Size(message = MsgOfValidationConstant.MSG_OF_PASSWORD_INVALID)
        String oldPassword,
        @NotBlank(message = MsgOfValidationConstant.MSG_OF_NEW_PASSWORD_REQUIRED)
        @Size(message = MsgOfValidationConstant.MSG_OF_PASSWORD_INVALID)
        String newPassword
) {
}
