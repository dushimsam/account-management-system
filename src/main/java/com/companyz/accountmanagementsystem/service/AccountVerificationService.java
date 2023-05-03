package com.companyz.accountmanagementsystem.service;

import com.companyz.accountmanagementsystem.dto.GlobalDtoGet;
import com.companyz.accountmanagementsystem.dto.accountverificationdto.GetAccountVerificationDto;
import com.companyz.accountmanagementsystem.dto.accountverificationdto.InitiateVerificationDto;
import com.companyz.accountmanagementsystem.enums.VerificationRequestStatus;
import com.companyz.accountmanagementsystem.enums.VerificationStatus;
import com.companyz.accountmanagementsystem.exception.ApiRequestException;
import com.companyz.accountmanagementsystem.model.AccountVerification;
import com.companyz.accountmanagementsystem.model.User;
import com.companyz.accountmanagementsystem.repository.AccountVerificationRepository;
import com.companyz.accountmanagementsystem.repository.UserRepository;
import com.companyz.accountmanagementsystem.service.reference.EmailSender;
import com.companyz.accountmanagementsystem.util.EmailUtil;
import com.companyz.accountmanagementsystem.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AccountVerificationService {
    @Autowired
    private AccountVerificationRepository accountVerificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailSender emailSender;

    @Value("${max.documentFile.size}")
    private long MAX_FILE_SIZE;

    @Value("${file.upload.document.dir}")
    private String documentDirectory;

    public ResponseEntity<?> initiateVerification(InitiateVerificationDto verificationDto){

        // check if the user exists
        Optional<User> optionalUser = userRepository.findById(verificationDto.getUserId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // check if the user is not already verified
        if (optionalUser.get().getVerificationStatus() == VerificationStatus.VERIFIED) {
            return ResponseEntity.badRequest().body(new ApiRequestException("User is already verified"));
        }

        // check if the user has a pending verification request
        if (optionalUser.get().getVerificationStatus() == VerificationStatus.PENDING_VERIFICATION) {
            return ResponseEntity.badRequest().body(new ApiRequestException("User has a pending verification request"));
        }

        AccountVerification accountVerification = new AccountVerification();
        accountVerification.setUser(optionalUser.get());
        accountVerification.setNidOrPassport(verificationDto.getNidOrPassport());
        accountVerificationRepository.save(accountVerification);

        // update the user verification status to PENDING_VERIFICATION
        optionalUser.get().setVerificationStatus(VerificationStatus.PENDING_VERIFICATION);
        userRepository.save(optionalUser.get());

        return ResponseEntity.ok(new GetAccountVerificationDto(accountVerification));
    }

    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(accountVerificationRepository.findAll());
    }

    public ResponseEntity<?> getById(UUID id){
        return  ResponseEntity.ok(new GetAccountVerificationDto(accountVerificationRepository.findById(id).get()));
    }
    public ResponseEntity<?> getAllByUser(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(accountVerificationRepository.findAllByUser(optionalUser.get()));
    }
    public ResponseEntity<?> getAllByStatus(VerificationRequestStatus verificationRequestStatus) {
        return ResponseEntity.ok(accountVerificationRepository.findAllByVerificationRequestStatus(verificationRequestStatus));
    }
    public ResponseEntity<?>  verifyAccount(UUID id) {
        AccountVerification accountVerification = accountVerificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Verification not found"));


        if (accountVerification.getVerificationRequestStatus() == VerificationRequestStatus.CONFIRMED) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ApiRequestException("Account Verification is already confirmed"));
        }

        if (accountVerification.getNidOrPassport() == null || accountVerification.getNidOrPassport().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Can not verify a user who has not uploaded a NID or Passport");
        }

        if (accountVerification.getOfficialDocument() == null || accountVerification.getOfficialDocument().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Can not verify a user who has not uploaded a document");
        }

        accountVerification.setVerificationRequestStatus(VerificationRequestStatus.CONFIRMED);
        accountVerificationRepository.save(accountVerification);

        User user = accountVerification.getUser();
        user.setVerificationStatus(VerificationStatus.VERIFIED);
        userRepository.save(user);

        emailSender.send(
                user.getEmail(),
                new EmailUtil().verifiedAccountEmail(user.getFirstName()), "CompanyZ: Congrats! Your Account is now verified");


        return ResponseEntity.ok(new GlobalDtoGet("Account Verification is confirmed successfully"));
    }

    public ResponseEntity<?> uploadDocument(UUID accountId, MultipartFile file) {
//        System.out.println("uploadOfficialDocument");

        // find user by id
        Optional<AccountVerification> optionalAccount = accountVerificationRepository.findById(accountId);
        if (optionalAccount.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Verification not found");
        }

        AccountVerification accountVerification = optionalAccount.get();

        ResponseEntity<?> res = new FileUtil(file).saveFile(MAX_FILE_SIZE, documentDirectory, accountVerification.getOfficialDocument());
        if (res.getStatusCode() != HttpStatus.OK)
            return res;

        accountVerification.setOfficialDocument(String.valueOf(res.getBody()));
        accountVerificationRepository.save(accountVerification);

        return ResponseEntity.ok(new GlobalDtoGet("Official Document is uploaded successfully"));
    }
}
