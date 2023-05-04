package com.companyz.accountmanagementsystem.service.reference;

public interface EmailSender {
    void send(String to, String email, String title);
}
