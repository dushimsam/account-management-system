package com.companyz.accountmanagementsystem.controller;

import com.companyz.accountmanagementsystem.dto.accountverificationdto.InitiateVerificationDto;
import com.companyz.accountmanagementsystem.enums.VerificationRequestStatus;
import com.companyz.accountmanagementsystem.service.AccountVerificationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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
    public ResponseEntity<?> getById(@PathVariable("id") UUID id) {
        return accountVerificationService.getById(id);
    }

    @PostMapping
    @CacheEvict(value = {"users","usersPaginated","userById", "usersByVerificationStatus", "usersByVerificationStatusPaginated"}, allEntries = true)
    public ResponseEntity<?> initiate(@RequestBody InitiateVerificationDto initiateVerificationDto) {
        return accountVerificationService.initiateVerification(initiateVerificationDto);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllByStatus(@PathVariable("status") VerificationRequestStatus status) {
        return accountVerificationService.getAllByStatus(status);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getAllByUserId(@PathVariable("id") UUID id) {
        return accountVerificationService.getAllByUser(id);
    }

    @PutMapping("/{verificationId}/official-document")
    @CacheEvict(value = {"users","usersPaginated","userById", "usersByVerificationStatus", "usersByVerificationStatusPaginated"}, allEntries = true)
    public ResponseEntity<?> uploadOfficialDocument(@PathVariable UUID verificationId, @RequestParam("file") MultipartFile file) {
        return accountVerificationService.uploadDocument(verificationId, file);
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasAuthority('ADMIN')")
    @CacheEvict(value = {"users","usersPaginated","userById", "usersByVerificationStatus", "usersByVerificationStatusPaginated"}, allEntries = true)
    public ResponseEntity<?> verifyAccount(@PathVariable("id") UUID id) {
        return accountVerificationService.verifyAccount(id);
    }

}
