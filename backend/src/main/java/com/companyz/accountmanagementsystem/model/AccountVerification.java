package com.companyz.accountmanagementsystem.model;


import com.companyz.accountmanagementsystem.enums.VerificationRequestStatus;
import com.companyz.accountmanagementsystem.enums.VerificationStatus;
import com.companyz.accountmanagementsystem.model.global.Auditable;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Table(name="accounts_verification")
public class AccountVerification extends Auditable<String> {
    @SequenceGenerator(
            name = "account_verification_sequence",
            sequenceName = "account_verification_sequence"
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "account_verification_sequence")
    private Long id;

    @NotNull
    private String nidOrPassport;

    @Nullable
    private String officialDocument;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "verificationRequestStatus", nullable = false)
    private VerificationRequestStatus verificationRequestStatus = VerificationRequestStatus.PENDING;
}
