package test.test.com.companyz.accountmanagementsystem.services;

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
import com.companyz.accountmanagementsystem.service.AccountVerificationService;
import com.companyz.accountmanagementsystem.service.reference.EmailSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AccountVerificationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountVerificationRepository accountVerificationRepository;

    @Mock
    private EmailSender emailSender;
    @InjectMocks
    private AccountVerificationService accountVerificationService;

    @Test
    public void initiateVerification_shouldReturnNotFound_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        InitiateVerificationDto verificationDto = new InitiateVerificationDto( "1234567890", userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = accountVerificationService.initiateVerification(verificationDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void initiateVerification_shouldReturnBadRequest_whenUserIsAlreadyVerified() {
        UUID userId = UUID.randomUUID();
        InitiateVerificationDto verificationDto = new InitiateVerificationDto( "1234567890", userId);
        User user = new User();
        user.setId(userId);
        user.setEmail("dushsam100@gmail.com");
        user.setVerificationStatus(VerificationStatus.VERIFIED);
        user.setPassword("XYZ");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = accountVerificationService.initiateVerification(verificationDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiRequestException);
        assertEquals("User is already verified", ((ApiRequestException) response.getBody()).getMessage());
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(accountVerificationRepository);
        verifyNoInteractions(emailSender);
    }

    @Test
    public void initiateVerification_shouldReturnBadRequest_whenUserHasPendingVerificationRequest() {
        UUID userId = UUID.randomUUID();
        InitiateVerificationDto verificationDto = new InitiateVerificationDto( "1234567890", userId);
        User user = new User();
        user.setId(userId);
        user.setEmail("dushsam100@gmail.com");
        user.setVerificationStatus(VerificationStatus.PENDING_VERIFICATION);
        user.setPassword("XYZ");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = accountVerificationService.initiateVerification(verificationDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiRequestException);
        assertEquals("User has a pending verification request", ((ApiRequestException) response.getBody()).getMessage());
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(accountVerificationRepository);
        verifyNoInteractions(emailSender);
    }

    @Test
    public void testGetAll() {
        ResponseEntity<?> response = accountVerificationService.getAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetAllByStatus() {
        // create a new AccountVerification with PENDING_VERIFICATION status
        AccountVerification accountVerification1 = new AccountVerification();
        accountVerification1.setNidOrPassport("123456");
        accountVerification1.setOfficialDocument("document1.jpg");
        accountVerification1.setVerificationRequestStatus(VerificationRequestStatus.PENDING);
        accountVerificationRepository.save(accountVerification1);

        // create a new AccountVerification with CONFIRMED status
        AccountVerification accountVerification2 = new AccountVerification();
        accountVerification2.setNidOrPassport("789012");
        accountVerification2.setOfficialDocument("document2.jpg");
        accountVerification2.setVerificationRequestStatus(VerificationRequestStatus.CONFIRMED);
        accountVerificationRepository.save(accountVerification2);

        // test getAllByStatus method
        ResponseEntity<?> response = accountVerificationService.getAllByStatus(VerificationRequestStatus.PENDING);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
    }

    @Test
    public void verifyAccount_ValidId_ReturnsOk() {
        // Arrange
        UUID id = UUID.randomUUID();
        AccountVerification accountVerification = new AccountVerification();
        accountVerification.setId(id);
        accountVerification.setVerificationRequestStatus(VerificationRequestStatus.PENDING);
        accountVerification.setNidOrPassport("1234567890");
        accountVerification.setOfficialDocument("document.jpg");
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");
        user.setFirstName("Samuel");
        accountVerification.setUser(user);

        when(accountVerificationRepository.findById(id)).thenReturn(Optional.of(accountVerification));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        ResponseEntity<?> response = accountVerificationService.verifyAccount(id);

        // Assert
        verify(accountVerificationRepository, times(1)).save(accountVerification);
        verify(userRepository, times(1)).save(user);
        verify(emailSender, times(1)).send(eq(user.getEmail()), anyString(), anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account Verification is confirmed successfully", ((GlobalDtoGet) response.getBody()).getMessage());
    }


}
