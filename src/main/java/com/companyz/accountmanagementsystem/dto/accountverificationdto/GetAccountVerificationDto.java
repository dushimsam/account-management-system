package com.companyz.accountmanagementsystem.dto.accountverificationdto;

import com.companyz.accountmanagementsystem.dto.userdto.GetUserDto;
import com.companyz.accountmanagementsystem.enums.VerificationRequestStatus;
import com.companyz.accountmanagementsystem.model.AccountVerification;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAccountVerificationDto {
    private UUID id;
    private String nidOrPassport;
    private String officialDocument;
    private GetUserDto user;
    private VerificationRequestStatus verificationRequestStatus;

    public GetAccountVerificationDto(AccountVerification accountVerification){
        BeanUtils.copyProperties(accountVerification,this);
        this.user = new GetUserDto(accountVerification.getUser(), "VERIFICATION");

        this.officialDocument = "http://localhost:4600/api/v1/image/load?path="+ getOfficialDocument();
    }
}
