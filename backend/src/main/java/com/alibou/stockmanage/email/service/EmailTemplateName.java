package com.alibou.stockmanage.email.service;

import lombok.Getter;

public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account.html", "Account activation"),
    PAYMENT_CONFIRMATION("payment-confirmation.html", "Payment successfully processed"),
    ORDER_CONFIRMATION("order_confirmation.html", "Order confirmation")
    ;

    @Getter
    private final String template;
    @Getter
    private final String subject;

    EmailTemplateName(String template, String subject){
        this.template = template;
        this.subject = subject;
    }

}
