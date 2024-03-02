package com.sake.examination_system.service.Imp;
import com.sake.examination_system.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class EmailServiceImp implements EmailService {
    @Value("${mail.sender.email}")
    private String emailSender;

    @Resource
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(emailSender);
        message.setSubject("验证码");
        message.setText("你的验证码是: " + verificationCode);
        mailSender.send(message);
    }
}
