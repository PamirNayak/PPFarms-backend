package com.pamir.ppfarmsbackend.shared.email.service;

import java.util.Map;

public interface EmailService {

    void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateModel);
}
