package com.companyz.accountmanagementsystem.service;

import com.companyz.accountmanagementsystem.dto.GlobalDtoGet;
import com.companyz.accountmanagementsystem.dto.userdto.PaginatedUserResponse;
import com.companyz.accountmanagementsystem.dto.userdto.GetUserDto;
import com.companyz.accountmanagementsystem.dto.userdto.CreateUserDto;
import com.companyz.accountmanagementsystem.enums.UserCategory;
import com.companyz.accountmanagementsystem.enums.VerificationStatus;
import com.companyz.accountmanagementsystem.exception.ApiRequestException;
import com.companyz.accountmanagementsystem.exception.NotFoundException;
import com.companyz.accountmanagementsystem.model.User;
import com.companyz.accountmanagementsystem.repository.UserRepository;
import com.companyz.accountmanagementsystem.util.FileUtil;
import com.companyz.accountmanagementsystem.validation.EmailValidator;
import com.companyz.accountmanagementsystem.validation.PasswordValidator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@AllArgsConstructor
@Service
@NoArgsConstructor(force = true)
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private PasswordValidator passwordValidator;

    private final EmailValidator emailValidator;

    @Value("${max.file.size}")
    private long MAX_FILE_SIZE;

    @Value("${file.upload.profile.dir}")
    private String profilePicDirectory;

    public ResponseEntity<?> createUser(CreateUserDto createUserDto) {
        User userExists = userRepository.findByEmail(createUserDto.getEmail());

        if (userExists != null) {
            return ResponseEntity.badRequest().body(new ApiRequestException("Email is already taken"));
        }

        boolean adminExists = userRepository.existsByCategory(UserCategory.ADMIN);

        if(!adminExists){
            createUserDto.setCategory(UserCategory.ADMIN);
        }else if(createUserDto.getCategory() == UserCategory.ADMIN){
            return ResponseEntity.badRequest().body(new ApiRequestException("Admin already exists"));
        }

        if (createUserDto.getDateOfBirth().after(new Date())) {
            return ResponseEntity.badRequest().body(new ApiRequestException("Date of birth cannot be in the future"));
        }

        String errorMessage = passwordValidator.isValidPassword(createUserDto.getPassword());

        if (errorMessage != null) {
            return ResponseEntity.badRequest().body(new ApiRequestException(errorMessage));
        }

        String encodedPassword = bCryptPasswordEncoder.encode(createUserDto.getPassword());

        User user = new User();
        BeanUtils.copyProperties(createUserDto, user);
        user.setPassword(encodedPassword);

        if (createUserDto.getCategory() == UserCategory.ADMIN) {
            user.setVerificationStatus(VerificationStatus.VERIFIED);
        }

        user = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GetUserDto(user));
    }


    public ResponseEntity<?> getAll() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(updateCopy(users));
    }

    public ResponseEntity<?> getAllPaginated(Pageable pageable) {
        Page<User> users = userRepository.findAllByCategory(UserCategory.CLIENT, pageable);

        return ResponseEntity.ok(PaginatedUserResponse.builder()
                .numberOfItems(users.getTotalElements()).numberOfPages(users.getTotalPages())
                .users(updateCopy(users.getContent()))
                .build());
    }
    public ResponseEntity<?> getAllByVerificationStatusPaginated(VerificationStatus verificationStatus, Pageable pageable) {
        Page<User> users = userRepository.findAllByVerificationStatusAndCategory(verificationStatus, UserCategory.CLIENT, pageable);
        return ResponseEntity.ok(PaginatedUserResponse.builder()
                .numberOfItems(users.getTotalElements()).numberOfPages(users.getTotalPages())
                .users(updateCopy(users.getContent()))
                .build());
    }

    public ResponseEntity<?> get(UUID userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundException("User not found"));
        }
        return ResponseEntity.ok(new GetUserDto(optionalUser.get()));
    }

    public ResponseEntity<?> getAllByVerificationStatus(VerificationStatus verificationStatus) {
        List<User> users = userRepository.findAllByVerificationStatus(verificationStatus);
        return ResponseEntity.ok(updateCopy(users));
    }


    public GetUserDto getUserByEmail(String email) {
        return new GetUserDto(userRepository.findByEmail(email));
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User userByEmail = userRepository.findByEmail(s);

        if (userByEmail != null) {
            return new org.springframework.security.core.userdetails.User(userByEmail.getUsername(), userByEmail.getPassword(), new ArrayList<>());
        }
        throw new ApiRequestException("Invalid Email or Password");
    }

    public ResponseEntity<?> enableTfa(UUID userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        user.setTfaEnabled(true);
        userRepository.save(user);

        return ResponseEntity.ok(new GlobalDtoGet("2TFA enabled successfully"));
    }

    public ResponseEntity<?> disableTfa(UUID userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        user.setTfaEnabled(false);
        userRepository.save(user);

        return ResponseEntity.ok().body(new GetUserDto(user));
    }

    public ResponseEntity<?> uploadProfilePic(UUID userId, MultipartFile file) {
        // find user by id
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        ResponseEntity<?> res = new FileUtil(file).saveFile(MAX_FILE_SIZE, profilePicDirectory, user.getProfileUrl().equals("") ? null : user.getProfileUrl());
        if (res.getStatusCode() != HttpStatus.OK)
            return res;

        user.setProfileUrl(String.valueOf(res.getBody()));
        userRepository.save(user);

        return ResponseEntity.ok(new GlobalDtoGet("Profile pic uploaded successfully"));
    }

    public List<GetUserDto> updateCopy(List<User> users) {
        List<GetUserDto> usersDto = new ArrayList<>();
        for (User item : users) {
            usersDto.add(new GetUserDto(item));
        }
        return usersDto;
    }

}


