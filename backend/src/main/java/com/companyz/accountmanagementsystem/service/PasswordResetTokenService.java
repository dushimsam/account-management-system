package com.companyz.accountmanagementsystem.service;

import com.companyz.accountmanagementsystem.model.PasswordResetToken;
import com.companyz.accountmanagementsystem.repository.PasswordResetTokenRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PasswordResetTokenService {
    @Autowired
    private PasswordResetTokenRepository repository;
    public void saveConfirmationToken(PasswordResetToken token) {
        repository.save(token);
    }

    public PasswordResetToken getToken(String token) {
        return repository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return repository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
