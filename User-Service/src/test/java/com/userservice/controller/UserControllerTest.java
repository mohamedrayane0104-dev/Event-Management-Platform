package com.userservice.controller;

import com.userservice.dto.LoginRequest;
import com.userservice.dto.LoginResponse;
import com.userservice.model.User;
import com.userservice.security.JwtUtil;
import com.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userService.register(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.register(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    void testLogin() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        when(userService.authenticate("testuser", "password123")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        ResponseEntity<?> response = userController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponse);
        LoginResponse body = (LoginResponse) response.getBody();
        assertEquals("jwt-token", body.getToken());
        assertEquals("testuser", body.getUser().getUsername());
    }

    @Test
    void testLoginFailed() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");

        when(userService.authenticate("testuser", "wrongpassword")).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
