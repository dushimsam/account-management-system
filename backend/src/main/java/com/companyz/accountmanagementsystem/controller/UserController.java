package com.companyz.accountmanagementsystem.controller;


import com.companyz.accountmanagementsystem.dto.userdto.CreateUserDto;
import com.companyz.accountmanagementsystem.enums.VerificationStatus;
import com.companyz.accountmanagementsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(path = "api/v1/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> add(@RequestBody CreateUserDto userDtoPost) {
        return userService.createUser(userDtoPost);
    }


    @GetMapping
    public ResponseEntity<?> getAll() {
        return userService.getAll();
    }

    @GetMapping("/paginated")
    public ResponseEntity<?> getAllPaginated(Pageable pageable) {
        return userService.getAllPaginated(pageable);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<?> get(
            @PathVariable("id") Long id) {
        return userService.get(id);
    }

    @GetMapping("/verification-status/{status}")
    public ResponseEntity<?> getByVerificationStatus(@PathVariable("status") VerificationStatus status) {
        return userService.getAllByVerificationStatus(status);
    }

    @GetMapping("/verification-status/{status}/paginated")
    public ResponseEntity<?> getByVerificationStatusPaginated(@PathVariable("status") VerificationStatus status, Pageable pageable) {
        return userService.getAllByVerificationStatusPaginated(status, pageable);
    }


    @PutMapping("/{userId}/profile-pic")
    public ResponseEntity<?> uploadProfilePic(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        return userService.uploadProfilePic(userId, file);
    }

    @PutMapping("/tfa/{id}")
    public ResponseEntity<?> enableTfa(@PathVariable("id") Long id) {
        return userService.enableTfa(id);
    }

    @PutMapping("/tfa-disable/{id}")
    public ResponseEntity<?> disableTfa(@PathVariable("id") Long id) {
        return userService.disableTfa(id);
    }
}
