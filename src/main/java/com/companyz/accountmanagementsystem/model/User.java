package com.companyz.accountmanagementsystem.model;

import com.companyz.accountmanagementsystem.enums.*;
import com.companyz.accountmanagementsystem.model.global.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

import com.sun.istack.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Table(name="users")
public class User extends Auditable<String> implements UserDetails {
    @Id
    @Type(type = "uuid-char")
    private UUID id = UUID.randomUUID();

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @Nullable
    private String profileUrl = "";

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @NotNull
    @Column(unique=true,nullable = false)
    private String email;

    @NotNull
    private Date dateOfBirth;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "verificationStatus", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    @JsonIgnore
    @NotNull
    @Column(nullable = false)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "maritalStatus", nullable = false)
    private MaritalStatus maritalStatus;


    @NotNull
    private Boolean tfaEnabled = false;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private UserCategory category = UserCategory.CLIENT;

    @Enumerated(EnumType.STRING)
    @Column(name = "nationality")
    private Nationality nationality = Nationality.RWANDAN;

    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<AccountVerification> accountVerifications = new ArrayList<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(category.name());
        return Collections.singletonList(authority);
    }
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
