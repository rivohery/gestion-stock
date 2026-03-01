package com.alibou.stockmanage.shared.utils;

import java.security.SecureRandom;

public class InvoiceNoGenerator {

    private static final String NUMERIC_CHARACTERS = "0123456789";

    public static String generateInvoiceNo(int length){
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(NUMERIC_CHARACTERS.length());
            codeBuilder.append(NUMERIC_CHARACTERS.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

}
