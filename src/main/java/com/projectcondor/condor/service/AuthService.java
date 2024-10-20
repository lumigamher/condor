package com.projectcondor.condor.service;

import com.projectcondor.condor.model.User;
import com.projectcondor.condor.repository.UserRepository;
import com.projectcondor.condor.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> signup(User user) {
        logger.info("Attempting to register user: {}", user.getUsername());
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            logger.warn("Username already exists: {}", user.getUsername());
            return ResponseEntity.badRequest().body("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        logger.info("User registered successfully: {}", user.getUsername());
        return ResponseEntity.ok("User registered successfully");
    }

    public ResponseEntity<?> login(User user) {
        logger.info("Attempting to authenticate user: {}", user.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.createToken(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            logger.info("User authenticated successfully: {}", userDetails.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("username", userDetails.getUsername());
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", user.getUsername(), e);
            return ResponseEntity.badRequest().body("Invalid username/password");
        }
    }
}