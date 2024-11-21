package com.projectcondor.condor.service;

import com.projectcondor.condor.model.User;
import com.projectcondor.condor.repository.UserRepository;
import com.projectcondor.condor.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User signup(User user) {
        // Codificar la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Guardar usuario
        return userRepository.save(user);
    }

    public ResponseEntity<?> login(User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.createToken(authentication);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("username", user.getUsername());

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Credenciales inválidas");
            return ResponseEntity.badRequest().body(response);
        }
    }
}