package com.companyz.accountmanagementsystem.dto.authdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TfaPostDto {
    private String token;
}
