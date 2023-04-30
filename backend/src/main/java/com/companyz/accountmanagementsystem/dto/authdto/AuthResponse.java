package com.companyz.accountmanagementsystem.dto.authdto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String extra = "LOGIN_SUCCESS";
  public AuthResponse(String token)
    {
        this.token = token;
    }
}
