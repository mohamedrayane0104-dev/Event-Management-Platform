package com.userservice.service;

import com.userservice.model.User;
import com.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        user.setPassword("password123");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register(user);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testAuthenticateSuccess() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        Optional<User> result = userService.authenticate("testuser", "password123");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void testAuthenticateFailed() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        Optional<User> result = userService.authenticate("testuser", "wrongpassword");

        assertFalse(result.isPresent());
    }
}