package com.alibou.stockmanage.auths.config;

public interface MsgOfValidationConstant {
    String MSG_OF_FIRSTNAME_REQUIRED = "Firstname is required";
    String MSG_OF_LASTNAME_REQUIRED = "Lastname is required";
    String MSG_OF_INVALID_FIRSTNAME= "Firstname must between 2 and 30 length";
    String MSG_OF_INVALID_LASTNAME= "Lastname must between 2 and 30 length";
    String MSG_OF_EMAIL_REQUIRED = "Email is required";
    String MSG_OF_INVALID_EMAIL = "Email is invalid";
    String MSG_OF_EMAIL_ALREADY_EXIST = "Email is already exist: %s";
    String MSG_OF_PHONE_NUMBER_REQUIRED = "Phone number is required";
    String MSG_OF_OLD_PASSWORD_REQUIRED = "Current password is required";
    String MSG_OF_NEW_PASSWORD_REQUIRED = "New password is required";
    String MSG_OF_PASSWORD_INVALID = "Length of password must between 4 and 25";
}
