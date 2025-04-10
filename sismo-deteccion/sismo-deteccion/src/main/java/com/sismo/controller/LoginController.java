package com.sismo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.sismo.service.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Crear token de autenticación
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            
            // Autenticar
            Authentication authentication = authenticationManager.authenticate(authToken);
            
            // Establecer la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Preparar respuesta
            LoginResponse response = new LoginResponse();
            response.setMessage("Login exitoso");
            response.setUsername(loginRequest.getUsername());
            response.setError(false);
            
            return ResponseEntity.ok(response);
            
        } catch (AuthenticationException e) {
            LoginResponse response = new LoginResponse();
            response.setMessage("Credenciales inválidas");
            response.setError(true);
            
            return ResponseEntity.ok(response);
        }
    }
    
    // Clase para la solicitud de login
    static class LoginRequest {
        private String username;
        private String password;
        
        // Getters y setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    // Clase para la respuesta de login
    static class LoginResponse {
        private String message;
        private String username;
        private boolean error;
        
        // Getters y setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public boolean isError() { return error; }
        public void setError(boolean error) { this.error = error; }
    }
}