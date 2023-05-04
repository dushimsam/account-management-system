package com.companyz.accountmanagementsystem.repository;

import com.companyz.accountmanagementsystem.enums.UserCategory;
import com.companyz.accountmanagementsystem.enums.VerificationStatus;
import com.companyz.accountmanagementsystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    public User findByEmail(String email);
    public User findUserByCategory(UserCategory category);
    public List<User> findAllByCategory(UserCategory category);

    public List<User> findAllByVerificationStatus(VerificationStatus verificationStatus);

    public Page<User> findAllByCategory(UserCategory category, Pageable pageable);
    public boolean existsByCategory(UserCategory category);

    public Page<User> findAllByVerificationStatus(VerificationStatus verificationStatus, Pageable pageable);
    public  Page<User> findAllByVerificationStatusAndCategory(VerificationStatus verificationStatus, UserCategory category, Pageable pageable);
}
