package com.symptomchecke.symptom_checker.service;

import com.symptomchecke.symptom_checker.entity.User;
import com.symptomchecke.symptom_checker.repository.UserRepository;
import com.symptomchecke.symptom_checker.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUser_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        String response = authService.registerUser(user);

        assertEquals("User registered successfully!", response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void loginUser_Success() {
        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = passwordEncoder.encode(password);
        String token = "mockJwtToken";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(email)).thenReturn(token);

        String result = authService.loginUser(email, password);

        assertEquals(token, result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void loginUser_InvalidPassword() {
        String email = "test@example.com";
        String password = "wrongPassword";
        String encodedPassword = "encodedPassword"; // Simulating encoded password

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        // Mock repository call to return the user
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Mock passwordEncoder.matches() behavior - This is the only necessary stub
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false); // Incorrect password scenario

        // Expect a RuntimeException when credentials are invalid
        Exception exception = assertThrows(RuntimeException.class, () -> authService.loginUser(email, password));
        assertTrue(exception.getMessage().contains("Invalid credentials"));

        // Verify interactions
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
    }

    @Test
    void loginUser_UserNotFound() {
        String email = "test@example.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> authService.loginUser(email, password));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    // Bean declaration for PasswordEncoder in the test context
    static class TestConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}