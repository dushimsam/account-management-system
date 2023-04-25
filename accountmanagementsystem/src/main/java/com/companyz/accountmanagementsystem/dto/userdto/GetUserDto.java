package com.companyz.accountmanagementsystem.dto.userdto;


import com.companyz.accountmanagementsystem.enums.*;
import com.companyz.accountmanagementsystem.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String profileUrl;
    private Gender gender;
    private String email;
    private Date dateOfBirth;
    private VerificationStatus verificationStatus;
    private MaritalStatus maritalStatus;
    private UserCategory category;
    private Nationality nationality;
    private Boolean tfaEnabled;
    private Date createdAt;
    private Date lastModifiedAt;
    public GetUserDto(User user){
        BeanUtils.copyProperties(user,this);
    }
}
