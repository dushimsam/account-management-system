package com.companyz.accountmanagementsystem.dto.userdto;


import com.companyz.accountmanagementsystem.dto.accountverificationdto.GetAccountVerificationDto;
import com.companyz.accountmanagementsystem.enums.*;
import com.companyz.accountmanagementsystem.model.AccountVerification;
import com.companyz.accountmanagementsystem.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserDto {
    private UUID id;
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
    private List<GetAccountVerificationDto> accountVerifications = new ArrayList<>();
    public GetUserDto(User user){
        BeanUtils.copyProperties(user,this);
        for(AccountVerification account: user.getAccountVerifications())
            accountVerifications.add(new GetAccountVerificationDto(account));
        this.profileUrl = "http://localhost:4600/api/v1/image/load?path="+ getProfileUrl();
    }
    public  GetUserDto(User user, String extra){
        BeanUtils.copyProperties(user,this);
    }
}
