package com.companyz.accountmanagementsystem.dto.authdto;


import lombok.Data;

@Data
public class ResetPasswordDto {
    private String newPassword;
    private String confirmPassword;
}
