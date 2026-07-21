package com.jsp.book.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailHelper {

    private final TemplateEngine templateEngine;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String fromEmail;

    private static final String FROM_NAME = "Book-My-Ticket";
    private static final String SUBJECT = "Otp for Creating Account with BookMyTicket";
    private static final String TEMPLATE = "email-template.html";
    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    @Async
    public void sendOtp(int otp, String name, String email) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("otp", otp);
            String htmlBody = templateEngine.process(TEMPLATE, context);

            Map<String, Object> sender = new HashMap<>();
            sender.put("name", FROM_NAME);
            sender.put("email", fromEmail);

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("email", email);
            recipient.put("name", name);

            Map<String, Object> payload = new HashMap<>();
            payload.put("sender", sender);
            payload.put("to", List.of(recipient));
            payload.put("subject", SUBJECT);
            payload.put("htmlContent", htmlBody);

            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", brevoApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_URL, request, String.class);

            log.info("OTP mail sent to {}, status: {}", email, response.getStatusCode());
        } catch (Exception ex) {
            log.error("Failed to send OTP mail for email: {}", email, ex);
        }
    }
}