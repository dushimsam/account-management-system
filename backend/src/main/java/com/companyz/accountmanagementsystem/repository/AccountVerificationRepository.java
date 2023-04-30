package com.companyz.accountmanagementsystem.repository;

import com.companyz.accountmanagementsystem.enums.VerificationRequestStatus;
import com.companyz.accountmanagementsystem.model.AccountVerification;
import com.companyz.accountmanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountVerificationRepository extends JpaRepository<AccountVerification, Long> {
 public AccountVerification findByUser(User user);
    public List<AccountVerification> findAllByUser(User user);
 public List<AccountVerification> findAllByVerificationRequestStatus(VerificationRequestStatus verificationRequestStatus);
}
