package com.medicare.backend.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendWelcomeEmail(String email, String name) {
        System.out.println("--------------------------------------------------");
        System.out.println("SIMULATED EMAIL SENT TO: " + email);
        System.out.println("Subject: Bienvenue sur MediCare+");
        System.out.println("Content: Bonjour " + name + ", votre compte a été créé avec succès.");
        System.out.println("--------------------------------------------------");
    }

    public void sendOtpSms(String phone, String code) {
        System.out.println("--------------------------------------------------");
        System.out.println("SIMULATED SMS SENT TO: " + phone);
        System.out.println("Body: Votre code de vérification MediCare+ est : " + code);
        System.out.println("--------------------------------------------------");
    }
}
