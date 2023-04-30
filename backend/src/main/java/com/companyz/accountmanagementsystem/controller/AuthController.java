package com.companyz.accountmanagementsystem.controller;

import com.companyz.accountmanagementsystem.dto.authdto.*;
import com.companyz.accountmanagementsystem.service.AuthService;
import com.companyz.accountmanagementsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/auth")
@AllArgsConstructor
public class AuthController {
    @Autowired
    private final AuthService authService;

    @PostMapping(path="/login")
    public ResponseEntity<?> generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        return authService.handleLogin(authRequest);
    }

    @PutMapping(path="/verify-token")
    public ResponseEntity<?> confirmTfa(@RequestBody TfaPostDto tfaPostDto) throws Exception {
        return authService.confirmTfaToken(tfaPostDto);
    }

    @PostMapping (path = "/reset-password/generate")
    public ResponseEntity<?> generateResetPasswordLink(@RequestBody GenerateResetLinkDto dto) throws Exception {
        return authService.generateResetPasswordLink(dto.getEmail());
    }

    @PutMapping(path="/reset-password/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable("token") String token, @RequestBody ResetPasswordDto resetPasswordDto) throws Exception {
        return authService.confirmResetPasswordToken(token, resetPasswordDto);
    }

}
