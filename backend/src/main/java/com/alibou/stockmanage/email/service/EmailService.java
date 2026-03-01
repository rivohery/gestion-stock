package com.alibou.stockmanage.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final String SOURCE_ADDRESS_EMAIL = "contact@aliboucoding.com";

    @Async
    public void sendActivateAccountEmail(
            String destinationEmail,
            String username,
            String confirmationUrl,
            String activationCode
    ) throws MessagingException {

        final String templateName = EmailTemplateName.ACTIVATE_ACCOUNT.getTemplate();
        final String subject = EmailTemplateName.ACTIVATE_ACCOUNT.getSubject();

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", username);
        variables.put("confirmationUrl", confirmationUrl);
        variables.put("activation_code", activationCode);

        sendEmail(destinationEmail, templateName, subject, variables);
    }

    @Async
    public void sendPaymentSuccessEmail(
            String destinationEmail,
            String customerName,
            BigDecimal amount,
            String orderReference
    ) throws MessagingException {

        final String templateName = EmailTemplateName.PAYMENT_CONFIRMATION.getTemplate();
        final String subject = EmailTemplateName.PAYMENT_CONFIRMATION.getSubject();

        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("amount", amount);
        variables.put("orderReference", orderReference);

        sendEmail(destinationEmail, templateName, subject, variables);
    }
    /*
    @Async
    public void sendOrderConfirmationEmail(
            String destinationEmail,
            String customerName,
            BigDecimal amount,
            String orderReference,
            List<Product> products
    ) throws MessagingException {

        final String templateName = EmailTemplateName.ORDER_CONFIRMATION.getTemplate();
        final String subject = EmailTemplateName.ORDER_CONFIRMATION.getSubject();

        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("totalAmount", amount);
        variables.put("orderReference", orderReference);
        variables.put("products", products);

        sendEmail(destinationEmail, templateName, subject, variables);
    }
    */
    private void sendEmail(
            @NonNull String destinationEmail,
            @NonNull String templateName,
            @NonNull String subject,
            @NonNull Map<String, Object> variables
    ) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                UTF_8.name()
        );
        messageHelper.setFrom(SOURCE_ADDRESS_EMAIL);

        Context context = new Context();
        context.setVariables(variables);
        messageHelper.setSubject(subject);

        String htmlTemplate = templateEngine.process(templateName, context);
        messageHelper.setText(htmlTemplate, true);
        messageHelper.setTo(destinationEmail);

        javaMailSender.send(mimeMessage);
        log.info(String.format("INFO - Email successfully sent to %s with template %s ", destinationEmail, templateName));

    }

}
