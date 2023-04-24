package com.companyz.accountmanagementsystem.controller;

import com.companyz.accountmanagementsystem.dto.authdto.AuthRequest;
import com.companyz.accountmanagementsystem.dto.authdto.AuthResponse;
import com.companyz.accountmanagementsystem.service.AuthService;
import com.companyz.accountmanagementsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1")
@AllArgsConstructor
public class AuthController {
    @Autowired
    private final AuthService authService;

    @PostMapping(path="/login")
    public ResponseEntity<AuthResponse> generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        return new ResponseEntity<AuthResponse>(authService.handleLogin(authRequest),HttpStatus.OK);
    }
}
