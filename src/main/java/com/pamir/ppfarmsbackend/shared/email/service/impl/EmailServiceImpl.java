package com.pamir.ppfarmsbackend.shared.email.service.impl;

import com.pamir.ppfarmsbackend.shared.email.service.EmailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    public EmailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @jakarta.annotation.PostConstruct
    public void logMailConfig() {
        // Gmail App Passwords are shown as "xxxx xxxx xxxx xxxx" — strip spaces automatically
        if (mailSender instanceof org.springframework.mail.javamail.JavaMailSenderImpl impl) {
            String cleaned = impl.getPassword() != null ? impl.getPassword().replaceAll("\\s+", "") : "";
            impl.setPassword(cleaned);
        }
        log.info("[MAIL CONFIG] Username resolved: '{}' | Password loaded: {}",
                fromAddress,
                (mailPassword == null || mailPassword.isBlank()) ? "❌ EMPTY (check .env)" : "✅ YES (" + mailPassword.replaceAll("\\s+", "").length() + " chars, spaces stripped)");
    }

    @Async
    @Override
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateModel) {
        try {
            Context context = new Context();
            context.setVariables(templateModel);

            String htmlContent = templateEngine.process("email/" + templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(new InternetAddress(fromAddress, "PP Farms", StandardCharsets.UTF_8.name()));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("[MAIL] Sent '{}' to [{}]", templateName, to);
        } catch (Exception e) {
            log.warn("[MAIL] Failed to send '{}' to [{}]: {}", templateName, to, e.getMessage());
        }
    }
}
