package com.alibou.stockmanage.auths.config;

public interface SecurityConstant {
    String FORGOT_PASSWORD_RESPONSE_MSG = "Un token a été envoyer sur votre adresse email pour activer votre compte";
    String USER_NOT_FOUND_MSG = "No user match with email: %s";
    String USER_DISABLED_MSG = "The user was disabled";
    String INVALID_TOKEN_MSG = "OTP token invalid";
    String OTP_TOKEN_EXPIRED_MSG = "Activation token has expired. A new token has been send to the same email address";
    int EXPIRATION_OTP_TOKEN = 15;
    int OTP_TOKEN_LENGTH = 6;
    String APPLICATION_ADMIN_EMAIL = "contact@aliboucoding.com";
}
