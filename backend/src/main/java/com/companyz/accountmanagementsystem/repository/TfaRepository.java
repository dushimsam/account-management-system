package com.companyz.accountmanagementsystem.repository;

import com.companyz.accountmanagementsystem.model.TfaToken;
import com.companyz.accountmanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
@Transactional(readOnly = true)
public interface TfaRepository extends JpaRepository<TfaToken, Long> {
    public TfaToken findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE TfaToken c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.token = ?1")
    int updateConfirmedAt(String token,
                          LocalDateTime confirmedAt);
}
