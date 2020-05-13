package com.ederfmatos.library.service.impl;

import com.ederfmatos.library.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${application.mail.remetent}")
    private String remetent;

    @Override
    public void sendMails(String message, List<String> emails) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remetent);
        mailMessage.setSubject("Livro com emprestimo atrasado");
        mailMessage.setText(message);

        mailMessage.setTo(emails.toArray(new String[emails.size()]));

        mailSender.send(mailMessage);
    }

}
