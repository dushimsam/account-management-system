package test.test.com.companyz.accountmanagementsystem.services;

import com.companyz.accountmanagementsystem.dto.GlobalDtoGet;
import com.companyz.accountmanagementsystem.dto.userdto.CreateUserDto;
import com.companyz.accountmanagementsystem.dto.userdto.GetUserDto;
import com.companyz.accountmanagementsystem.enums.UserCategory;
import com.companyz.accountmanagementsystem.enums.VerificationStatus;
import com.companyz.accountmanagementsystem.exception.ApiRequestException;
import com.companyz.accountmanagementsystem.model.User;
import com.companyz.accountmanagementsystem.repository.UserRepository;
import com.companyz.accountmanagementsystem.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotSame;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateUser() {
        // Setup
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setEmail("test@test.com");
        createUserDto.setPassword("test123");
        createUserDto.setDateOfBirth(new Date());
        createUserDto.setCategory(UserCategory.CLIENT);

        User existingUser = new User();
        existingUser.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com")).thenReturn(existingUser);

        // Execution
        ResponseEntity<?> response = userService.createUser(createUserDto);

        // Verification
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiRequestException);
        ApiRequestException exception = (ApiRequestException) response.getBody();
        assertEquals("Email is already taken", exception.getMessage());
    }

    @Test
    public void testGetAll() {
        // Create a list of User objects
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setFirstName("Samuel");
        user1.setLastName("Dushimimana");
        user1.setEmail("samueldush@example.com");
        users.add(user1);

        User user2 = new User();
        user2.setFirstName("Sam");
        user2.setLastName("Dush");
        user2.setEmail("dushsam@example.com");
        users.add(user2);

        // Mock the behavior of userRepository.findAll()
        when(userRepository.findAll()).thenReturn(users);

        // Call the method being tested
        ResponseEntity<?> response = userService.getAll();

        // Verify that the response status is OK
        assertEquals(200, response.getStatusCodeValue());

        List<GetUserDto> updateUsers = updateCopy(users);

        // Verify that the returned list is a copy of the original list
        List<User> updatedUsers = (List<User>) response.getBody();
        assertNotSame(updateUsers, updatedUsers);
        assertEquals(updateUsers.size(), updatedUsers.size());
        assertEquals(updateUsers.get(0), updatedUsers.get(0));
        assertEquals(updateUsers.get(1), updatedUsers.get(1));
    }

    @Test
    public void testGetExistingUser() {
        // Create a mock User object
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("Samuel");
        user.setLastName("Dushimimana");
        user.setEmail("dushsam@example.com");

        // Mock the behavior of userRepository.findById()
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Call the method being tested
        ResponseEntity<?> response = userService.get(user.getId());

        // Verify that the response status is OK
        assertEquals(200, response.getStatusCodeValue());

        // Verify that the returned DTO matches the original User object
        GetUserDto dto = (GetUserDto) response.getBody();
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getFirstName(), dto.getFirstName());
        assertEquals(user.getLastName(), dto.getLastName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    public void testGetAllByVerificationStatus() {
        // Create some dummy users
        User user1 = new User();
        user1.setVerificationStatus(VerificationStatus.VERIFIED);
        User user2 = new User();
        user2.setVerificationStatus(VerificationStatus.PENDING_VERIFICATION);
        List<GetUserDto> users = updateCopy(Arrays.asList(user1, user2));


        // Set up the mock behavior
        when(userRepository.findAllByVerificationStatus(VerificationStatus.VERIFIED)).thenReturn(Collections.singletonList(user1));

        // Call the method being tested
        ResponseEntity<?> response = userService.getAllByVerificationStatus(VerificationStatus.VERIFIED);


        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<GetUserDto> resultUsers = (List<GetUserDto>) response.getBody();
        assertEquals(1, resultUsers.size());
        assertEquals(VerificationStatus.VERIFIED, resultUsers.get(0).getVerificationStatus());
    }

    @Test
    public void testGetUserByEmail() {
        // Create a dummy user
        User user = new User();
        user.setEmail("test@example.com");

        // Set up the mock behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        // Call the method being tested
        GetUserDto result = userService.getUserByEmail("test@example.com");

        // Verify the results
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    public void testEnableTfa() {
        // Create a dummy user
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setTfaEnabled(false);

        // Set up the mock behavior
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).then(AdditionalAnswers.returnsFirstArg());

        // Call the method being tested
        ResponseEntity<?> response = userService.enableTfa(user.getId());

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(user.getTfaEnabled());
        assertEquals("2TFA enabled successfully", ((GlobalDtoGet) response.getBody()).getMessage());
    }


    public List<GetUserDto> updateCopy(List<User> users) {
        List<GetUserDto> usersDto = new ArrayList<>();
        for (User item : users) {
            usersDto.add(new GetUserDto(item));
        }
        return usersDto;
    }
}
