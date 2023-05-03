package com.companyz.accountmanagementsystem.controller;


import com.companyz.accountmanagementsystem.dto.userdto.CreateUserDto;
import com.companyz.accountmanagementsystem.enums.VerificationStatus;
import com.companyz.accountmanagementsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@RestController
@RequestMapping(path = "api/v1/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @PostMapping
    @CacheEvict(value = {"users","usersPaginated","userById", "usersByVerificationStatus", "usersByVerificationStatusPaginated"}, allEntries = true)
    public ResponseEntity<?> add(@RequestBody CreateUserDto userDtoPost) {
        return userService.createUser(userDtoPost);
    }



    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    @Cacheable("users")
    public ResponseEntity<?> getAll() {
        return userService.getAll();
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Cacheable("usersPaginated")
    public ResponseEntity<?> getAllPaginated(Pageable pageable) {
        return userService.getAllPaginated(pageable);
    }


    @GetMapping(path = "{id}")
    @Cacheable(value = "userById", key = "#id")
    public ResponseEntity<?> get(@PathVariable("id") UUID id) {
        return userService.get(id);
    }


    @GetMapping("/verification-status/{status}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Cacheable(value = "usersByVerificationStatus", key = "#status.toString()")
    public ResponseEntity<?> getByVerificationStatus(@PathVariable("status") VerificationStatus status) {
        return userService.getAllByVerificationStatus(status);
    }


    @GetMapping("/verification-status/{status}/paginated")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Cacheable(value = "usersByVerificationStatusPaginated", key = "{#status.toString(), #pageable}")
    public ResponseEntity<?> getByVerificationStatusPaginated(@PathVariable("status") VerificationStatus status, Pageable pageable) {
        return userService.getAllByVerificationStatusPaginated(status, pageable);
    }



    @PutMapping("/{userId}/profile-pic")
    @CacheEvict(value = {"users","usersPaginated","userById", "userByUsername", "usersByVerificationStatus", "usersByVerificationStatusPaginated"}, allEntries = true)
    public ResponseEntity<?> uploadProfilePic(@PathVariable UUID userId, @RequestParam("file") MultipartFile file) {
        return userService.uploadProfilePic(userId, file);
    }


    @PutMapping("/tfa/{id}")
    @CacheEvict(value = {"userById", "userByUsername"}, allEntries = true)
    public ResponseEntity<?> enableTfa(@PathVariable("id") UUID id) {
        return userService.enableTfa(id);
    }


    @PutMapping("/tfa-disable/{id}")
    @CacheEvict(value = {"userById", "userByUsername"}, allEntries = true)
    public ResponseEntity<?> disableTfa(@PathVariable("id") UUID id) {
        return userService.disableTfa(id);
    }

}
