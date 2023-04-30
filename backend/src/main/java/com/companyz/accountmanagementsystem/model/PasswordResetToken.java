package com.companyz.accountmanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Table(name="password_reset_tokens")
public class PasswordResetToken {
    @SequenceGenerator(
            name = "password_reset_token_sequence",
            sequenceName = "password_reset_token_sequence"
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "password_reset_token_sequence")
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

//    @ManyToOne
//    @JoinColumn(
//            nullable = false,
//            name = "user_id"
//    )
//    private User user;

    public PasswordResetToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
//        this.confirmedAt = confirmedAt;
//        this.user = user;
    }
}
