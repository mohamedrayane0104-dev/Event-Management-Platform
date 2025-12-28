package com.userservice.controller;

import com.userservice.dto.LoginRequest;
import com.userservice.dto.LoginResponse;
import com.userservice.model.User;
import com.userservice.service.UserService;
import com.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/auth/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if (user.isPresent()) {
            String token = jwtUtil.generateToken(user.get());
            return ResponseEntity.ok(new LoginResponse(token, user.get()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
