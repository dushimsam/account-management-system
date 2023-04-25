package com.companyz.accountmanagementsystem.dto.accountverificationdto;

import com.companyz.accountmanagementsystem.model.AccountVerification;
import com.companyz.accountmanagementsystem.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAccountVerificationDto {
    private Long id;
    private String nidOrPassport;
    private String officialDocument;
    private User user;
    private String verificationRequestStatus;
    public GetAccountVerificationDto(AccountVerification accountVerification){
        BeanUtils.copyProperties(accountVerification,this);
    }
}
