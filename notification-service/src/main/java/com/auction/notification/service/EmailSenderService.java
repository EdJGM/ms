package com.auction.notification.service;

public interface EmailSenderService {
    void sendEmail(String email, String subject, String text);
    void sendEmailWithSender(String email, String subject, String text, String sentBy);
}
