package com.ederfmatos.library.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmailService {
    
    void sendMails(List<String> emails);

}
