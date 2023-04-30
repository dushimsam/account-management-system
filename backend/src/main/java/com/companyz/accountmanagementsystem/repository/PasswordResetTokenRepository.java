package com.companyz.accountmanagementsystem.repository;

import com.companyz.accountmanagementsystem.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
@Transactional(readOnly = true)
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    public PasswordResetToken findByToken(String token);

//    public PasswordResetToken findByUserEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE PasswordResetToken c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.token = ?1")
    int updateConfirmedAt(String token,
                          LocalDateTime confirmedAt);
}
