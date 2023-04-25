package com.companyz.accountmanagementsystem.controller;

import com.companyz.accountmanagementsystem.dto.authdto.AuthRequest;
import com.companyz.accountmanagementsystem.dto.authdto.AuthResponse;
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

    @PutMapping(path="/tfa/{token}/confirm")
    public ResponseEntity<?> confirmTfa(@PathVariable("token") String token) throws Exception {
        return authService.confirmTfaToken(token);
    }
}
