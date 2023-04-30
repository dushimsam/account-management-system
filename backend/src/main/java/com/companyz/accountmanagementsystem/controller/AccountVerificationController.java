package com.companyz.accountmanagementsystem.controller;

import com.companyz.accountmanagementsystem.dto.accountverificationdto.InitiateVerificationDto;
import com.companyz.accountmanagementsystem.enums.VerificationRequestStatus;
import com.companyz.accountmanagementsystem.service.AccountVerificationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    //get by id
    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        return accountVerificationService.getById(id);
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

    @PostMapping("/{verificationId}/official-document")
    public ResponseEntity<?> uploadOfficialDocument(@PathVariable Long verificationId, @RequestParam("file") MultipartFile file) {
        return accountVerificationService.uploadDocument(verificationId, file);
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verifyAccount(@PathVariable("id") Long id) {
        return accountVerificationService.verifyAccount(id);
    }




}
