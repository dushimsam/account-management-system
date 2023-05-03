package com.companyz.accountmanagementsystem.service;

import com.companyz.accountmanagementsystem.dto.GlobalDtoGet;
import com.companyz.accountmanagementsystem.dto.authdto.AuthRequest;
import com.companyz.accountmanagementsystem.dto.authdto.AuthResponse;
import com.companyz.accountmanagementsystem.dto.authdto.ResetPasswordDto;
import com.companyz.accountmanagementsystem.dto.authdto.TfaPostDto;
import com.companyz.accountmanagementsystem.dto.userdto.GetUserDto;
import com.companyz.accountmanagementsystem.exception.ApiRequestException;
import com.companyz.accountmanagementsystem.model.ResetToken;
import com.companyz.accountmanagementsystem.model.TfaToken;
import com.companyz.accountmanagementsystem.model.User;
import com.companyz.accountmanagementsystem.repository.UserRepository;
import com.companyz.accountmanagementsystem.service.reference.EmailSender;
import com.companyz.accountmanagementsystem.util.EmailUtil;
import com.companyz.accountmanagementsystem.util.JwtUtil;
import com.companyz.accountmanagementsystem.validation.PasswordValidator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private  UserService userService;

    @Autowired
    private TfaTokenService tfaTokenService;

    @Autowired
    private ResetTokenService resetTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordValidator passwordValidator;

    @Autowired
    private EmailSender emailSender;

//    @Autowired
//    private PasswordResetTokenService passwordResetService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${client.base.url}")
    private String clientBaseUrl;


    public ResponseEntity<?> handleLogin(AuthRequest authRequest) throws Exception{
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (Exception ex) {
            throw new Exception("Invalid userName/password");
        }

        GetUserDto userDto = userService.getUserByEmail(authRequest.getEmail());

        if(userDto.getTfaEnabled() == null || !userDto.getTfaEnabled())
          return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(userDto)));

        String token = UUID.randomUUID().toString().substring(0, 5).toUpperCase();


        User user = userRepository.findByEmail(authRequest.getEmail());


        TfaToken tfaToken = new TfaToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        tfaTokenService.saveConfirmationToken(
                tfaToken);

        emailSender.send(
                user.getEmail(),
                new EmailUtil().buildConfirmTokenEmail(user.getFirstName(), token), "CompanyZ - 2FA");

        return ResponseEntity.ok(new AuthResponse("2FA token sent to your email address", "2FA"));
    }

    public ResponseEntity<?> confirmTfaToken(TfaPostDto tfaPostDto) {
        TfaToken confirmationToken = tfaTokenService
                .getToken(tfaPostDto.getToken());

        if(confirmationToken == null)
            return ResponseEntity.status(401).body(new GlobalDtoGet("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Code already used");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
             return ResponseEntity.status(401).body(new GlobalDtoGet("Token expired"));
        }

        tfaTokenService.setConfirmedAt(tfaPostDto.getToken());

        return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(new GetUserDto(confirmationToken.getUser()))));

    }

    public ResponseEntity<?> generateResetPasswordLink(String email){
        User user = userRepository.findByEmail(email);
        if(user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GlobalDtoGet("User not found"));

        String token = user.getId()+UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        ResetToken passwordReset = new ResetToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        resetTokenService.saveConfirmationToken(passwordReset);

        emailSender.send(
                user.getEmail(),
                new EmailUtil().buildResetPasswordEmail(user.getFirstName(), clientBaseUrl+"/reset-password?token="+token), "CompanyZ - ResetPassword");

        return ResponseEntity.ok(new GlobalDtoGet("Reset Password link sent on your email"));

    }
    public ResponseEntity<?> confirmResetPasswordToken(String token, ResetPasswordDto resetPasswordDto) {

        ResetToken passwordReset = resetTokenService.getToken(token);

        if(!Objects.equals(resetPasswordDto.getNewPassword(), resetPasswordDto.getConfirmPassword()))
            return ResponseEntity.status(401).body(new GlobalDtoGet("Password don't match"));

        String errorMessage = passwordValidator.isValidPassword(resetPasswordDto.getNewPassword());

        if (errorMessage != null) {
            return ResponseEntity.badRequest().body(new ApiRequestException(errorMessage));
        }

        if(passwordReset == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GlobalDtoGet("Token Not found"));

        if (passwordReset.getConfirmedAt() != null) {
            return ResponseEntity.status(401).body(new GlobalDtoGet("Code already used"));
        }

        LocalDateTime expiredAt = passwordReset.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(401).body(new GlobalDtoGet("Token expired"));
        }

        String encodedNewPass = bCryptPasswordEncoder.encode(resetPasswordDto.getNewPassword());

        User user = passwordReset.getUser();
        user.setPassword(encodedNewPass);
        userRepository.save(user);
        resetTokenService.setConfirmedAt(token);

        return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(new GetUserDto(passwordReset.getUser()))));
    }

    public ResponseEntity<?> getSelf(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GlobalDtoGet("User not logged in"));
        }
        String username = authentication.getName();
        GetUserDto user = userService.getUserByEmail(username);
        return ResponseEntity.ok(user);
    }
}
