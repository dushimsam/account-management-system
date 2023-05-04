package com.companyz.accountmanagementsystem.dto.accountverificationdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitiateVerificationDto {

    @NotNull
    private String nidOrPassport;

    @NotNull
    private UUID userId;
}
