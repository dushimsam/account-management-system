package com.companyz.accountmanagementsystem.service;


import com.companyz.accountmanagementsystem.model.ResetToken;
import com.companyz.accountmanagementsystem.repository.ResetTokenRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ResetTokenService {
    @Autowired
    private ResetTokenRepository resetTokenRepository;
    public void saveConfirmationToken(ResetToken token) {
        resetTokenRepository.save(token);
    }
    public ResetToken getToken(String token) {
        return resetTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return resetTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
