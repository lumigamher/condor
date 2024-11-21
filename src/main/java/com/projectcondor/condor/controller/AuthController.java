package com.projectcondor.condor.controller;

import com.projectcondor.condor.model.User;
import com.projectcondor.condor.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            logger.info("Recibida solicitud de registro para usuario: {}", user.getUsername());
            
            // Validar que el usuario no exista
            if (authService.existsByUsername(user.getUsername())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "El nombre de usuario ya existe");
                return ResponseEntity.badRequest().body(response);
            }

            // Intentar registrar el usuario
            User savedUser = authService.signup(user);
            
            // Preparar respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            response.put("userId", savedUser.getId());
            response.put("username", savedUser.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error en el registro: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error en el registro: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            logger.info("Recibida solicitud de login para usuario: {}", user.getUsername());
            return authService.login(user);
        } catch (Exception e) {
            logger.error("Error en el login: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error en el login: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}