
package expensetracker.application.services;

import expensetracker.application.dto.UserResponse;
import expensetracker.application.entities.User;
import expensetracker.application.exceptions.DuplicateEmailException;
import expensetracker.application.repositories.UserRepository;
import expensetracker.application.dto.UserRegistrationRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUserSuccessfully() {

        UserRegistrationRequest request=new UserRegistrationRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        User savedUser=new User();
        savedUser.setName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("password123");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword123");

        UserResponse result=userService.register(request);

        assertNotNull(result);
        assertEquals("Test User",result.getName());
        assertEquals("test@example.com",result.getEmail());

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUserWithDuplicateEmail(){
        UserRegistrationRequest request=new UserRegistrationRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        assertThrows(DuplicateEmailException.class,()->{
            userService.register(request);
        });

        verify(userRepository,never()).save(any(User.class));
    }
}
