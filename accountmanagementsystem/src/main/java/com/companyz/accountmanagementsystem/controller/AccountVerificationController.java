package com.companyz.accountmanagementsystem.controller;

import com.companyz.accountmanagementsystem.dto.accountverificationdto.InitiateVerificationDto;
import com.companyz.accountmanagementsystem.enums.VerificationRequestStatus;
import com.companyz.accountmanagementsystem.service.AccountVerificationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/account-verification")
@AllArgsConstructor
public class AccountVerificationController {

    @Autowired
    private final AccountVerificationService accountVerificationService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return accountVerificationService.getAll();
    }

    @PostMapping
    public ResponseEntity<?> initiate(@RequestBody InitiateVerificationDto initiateVerificationDto) {
        return accountVerificationService.initiateVerification(initiateVerificationDto);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAllByStatus(@PathVariable("status") VerificationRequestStatus status) {
        return accountVerificationService.getAllByStatus(status);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getAllByUserId(@PathVariable("id") Long id) {
        return accountVerificationService.getAllByUser(id);
    }

    @PutMapping("/verify/{id}")
    public ResponseEntity<?> verifyAccount(@PathVariable("id") Long id) {
        return accountVerificationService.verifyAccount(id);
    }
}
