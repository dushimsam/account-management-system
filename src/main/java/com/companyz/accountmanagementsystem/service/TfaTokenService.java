package com.companyz.accountmanagementsystem.service;

import com.companyz.accountmanagementsystem.model.TfaToken;
import com.companyz.accountmanagementsystem.repository.TfaRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TfaTokenService {
    @Autowired
    private TfaRepository tfaRepository;
    public void saveConfirmationToken(TfaToken token) {
        tfaRepository.save(token);
    }

    public TfaToken getToken(String token) {
        return tfaRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return tfaRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
