package test.test.com.companyz.accountmanagementsystem.services;

import com.companyz.accountmanagementsystem.dto.GlobalDtoGet;
import com.companyz.accountmanagementsystem.dto.authdto.AuthRequest;
import com.companyz.accountmanagementsystem.dto.authdto.AuthResponse;
import com.companyz.accountmanagementsystem.dto.authdto.ResetPasswordDto;
import com.companyz.accountmanagementsystem.dto.authdto.TfaPostDto;
import com.companyz.accountmanagementsystem.dto.userdto.GetUserDto;
import com.companyz.accountmanagementsystem.model.ResetToken;
import com.companyz.accountmanagementsystem.model.TfaToken;
import com.companyz.accountmanagementsystem.model.User;
import com.companyz.accountmanagementsystem.repository.UserRepository;
import com.companyz.accountmanagementsystem.service.AuthService;
import com.companyz.accountmanagementsystem.service.ResetTokenService;
import com.companyz.accountmanagementsystem.service.TfaTokenService;
import com.companyz.accountmanagementsystem.service.UserService;
import com.companyz.accountmanagementsystem.service.reference.EmailSender;
import com.companyz.accountmanagementsystem.util.JwtUtil;
import com.companyz.accountmanagementsystem.validation.PasswordValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private ResetTokenService resetTokenService;

    @Mock
    private JwtUtil jwtUtil;


    @Mock
    private TfaTokenService tfaTokenService;

    @Mock
    private EmailSender emailSender;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private PasswordValidator passwordValidator;

    @InjectMocks
    private AuthService authService;

    @Test
    public void testHandleLoginWithValidCredentials() throws Exception {
        // Arrange
        AuthRequest authRequest = new AuthRequest("user@example.com", "password");
        GetUserDto userDto = new GetUserDto();
        userDto.setEmail("user@example.com");
        userDto.setTfaEnabled(false);
        when(userService.getUserByEmail("user@example.com")).thenReturn(userDto);
        when(jwtUtil.generateToken(userDto)).thenReturn("test_token");

        // Act
        ResponseEntity<?> response = authService.handleLogin(authRequest);

        // Assert
        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        verify(userService, times(1)).getUserByEmail("user@example.com");
        verify(jwtUtil, times(1)).generateToken(userDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponse);
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertEquals("test_token", authResponse.getToken());
    }

    @Test
    public void testConfirmTfaToken() {
        // Setup
        TfaPostDto tfaPostDto = new TfaPostDto("12345");
        User user = new User();
        user.setId(UUID.randomUUID());
        TfaToken token = new TfaToken("12345", LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), user);
        when(tfaTokenService.getToken(tfaPostDto.getToken())).thenReturn(token);
        when(jwtUtil.generateToken(any(GetUserDto.class))).thenReturn("jwt-token");

        // Execution
        ResponseEntity<?> response = authService.confirmTfaToken(tfaPostDto);

        // Verification
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertNotNull(authResponse);
        assertEquals("jwt-token", authResponse.getToken());
        verify(tfaTokenService, times(1)).setConfirmedAt(tfaPostDto.getToken());
    }

    @Test
    public void testConfirmTfaToken_tokenNotFound() {
        // Setup
        TfaPostDto tfaPostDto = new TfaPostDto("invalid-token");
        when(tfaTokenService.getToken(tfaPostDto.getToken())).thenReturn(null);

        // Execution
        ResponseEntity<?> response = authService.confirmTfaToken(tfaPostDto);

        // Verification
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        GlobalDtoGet errorResponse = (GlobalDtoGet) response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Token not found", errorResponse.getMessage());
    }

    @Test
    public void testConfirmTfaToken_tokenExpired() {
        // Setup
        TfaPostDto tfaPostDto = new TfaPostDto("expired-token");
        User user = new User();
        user.setId(UUID.randomUUID());
        TfaToken token = new TfaToken("expired-token", LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(5), user);
        when(tfaTokenService.getToken(tfaPostDto.getToken())).thenReturn(token);

        // Execution
        ResponseEntity<?> response = authService.confirmTfaToken(tfaPostDto);

        // Verification
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        GlobalDtoGet errorResponse = (GlobalDtoGet) response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Token expired", errorResponse.getMessage());
    }

    @Test
    public void testConfirmTfaToken_tokenAlreadyUsed() {
        // Setup
        TfaPostDto tfaPostDto = new TfaPostDto("used-token");
        User user = new User();
        user.setId(UUID.randomUUID());
        TfaToken token = new TfaToken("used-token", LocalDateTime.now().minusMinutes(5), LocalDateTime.now().plusMinutes(5), user);
        token.setConfirmedAt(LocalDateTime.now());
        when(tfaTokenService.getToken(tfaPostDto.getToken())).thenReturn(token);

        // Execution and verification
        assertThrows(IllegalStateException.class, () -> authService.confirmTfaToken(tfaPostDto));
    }

    @Test
    public void testGenerateResetPasswordLink() {
        // Mock user data
        String email = "johndoe@example.com";
        User user = new User();
        user.setEmail(email);
        user.setFirstName("John");

        // Mock repository behavior
        when(userRepository.findByEmail(email)).thenReturn(user);

        // Call the method under test
        ResponseEntity<?> responseEntity = authService.generateResetPasswordLink(email);

        // Verify that a success response was returned
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof GlobalDtoGet);

        GlobalDtoGet response = (GlobalDtoGet) responseEntity.getBody();
        assertEquals("Reset Password link sent on your email", response.getMessage());

        // Verify that a reset token was generated and saved
        ArgumentCaptor<ResetToken> tokenCaptor = ArgumentCaptor.forClass(ResetToken.class);
        verify(resetTokenService, times(1)).saveConfirmationToken(tokenCaptor.capture());

        ResetToken resetToken = tokenCaptor.getValue();
        assertNotNull(resetToken);
        assertEquals(user, resetToken.getUser());

        // Verify that an email was sent with the correct data
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailSender, times(1)).send(emailCaptor.capture(), messageCaptor.capture(), subjectCaptor.capture());

        String sentEmail = emailCaptor.getValue();
        String sentSubject = subjectCaptor.getValue();
        String sentMessage = messageCaptor.getValue();

        assertEquals(email, sentEmail);
        assertEquals("CompanyZ - ResetPassword", sentSubject);
        assertTrue(sentMessage.contains(user.getFirstName()));
        assertTrue(sentMessage.contains(resetToken.getToken()));
    }

    @Test
    public void testGenerateResetPasswordLink_userNotFound() {
        // Mock repository behavior
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        // Call the method under test
        ResponseEntity<?> responseEntity = authService.generateResetPasswordLink("nonexistent@example.com");

        // Verify that a not found response was returned
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof GlobalDtoGet);

        GlobalDtoGet response = (GlobalDtoGet) responseEntity.getBody();
        assertEquals("User not found", response.getMessage());

        // Verify that no token was generated or saved
        verify(resetTokenService, never()).saveConfirmationToken(any());
    }


    @Test
    public void testConfirmResetPasswordToken() {
        // create test data
        String token = "test-token";
        ResetToken resetToken = new ResetToken();
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");
        resetToken.setUser(user);

        // mock dependencies
        when(resetTokenService.getToken(token)).thenReturn(resetToken);
        when(bCryptPasswordEncoder.encode("new-password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(any(GetUserDto.class))).thenReturn("test-jwt-token");

        // test confirmResetPasswordToken
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setNewPassword("new-password");
        resetPasswordDto.setConfirmPassword("new-password");

        ResponseEntity<?> response = authService.confirmResetPasswordToken(token, resetPasswordDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponse);
        assertEquals("test-jwt-token", ((AuthResponse) response.getBody()).getToken());

        verify(resetTokenService, times(1)).getToken(token);
        verify(bCryptPasswordEncoder, times(1)).encode("new-password");
        verify(userRepository, times(1)).save(any(User.class));
        verify(resetTokenService, times(1)).setConfirmedAt(token);
        verify(jwtUtil, times(1)).generateToken(any(GetUserDto.class));
    }
}
