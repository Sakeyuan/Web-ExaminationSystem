package com.sake.examination_system.service;

public interface EmailService {
    void sendEmail(String to, String verificationCode);
}
