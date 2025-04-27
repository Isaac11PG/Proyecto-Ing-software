package com.sismo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import com.sismo.service.UsuarioService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
    try {
        // Extraer las credenciales
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        
        System.out.println("Intento de login para usuario: " + username);
        
        // Crear token de autenticación
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(username, password);
        
        // Autenticar
        Authentication authentication = authenticationManager.authenticate(authToken);
        
        System.out.println("Autenticación exitosa para: " + authentication.getName());
        System.out.println("Roles: " + authentication.getAuthorities());
        
        // Establecer la autenticación en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Obtener o crear la sesión HTTP y guardar el SecurityContext
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        
        // Establecer una cookie de sesión explícita (como backup)
        Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setMaxAge(-1); // -1 significa que expira cuando se cierra el navegador
        response.addCookie(sessionCookie);
        
        System.out.println("ID de sesión creada: " + session.getId());
        
        // Extraer roles del usuario autenticado
        List<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
        
        // Crear un mapa para la respuesta
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "Login exitoso");
        responseMap.put("username", username);
        responseMap.put("error", false);
        responseMap.put("roles", roles);
        responseMap.put("sessionId", session.getId()); // Incluir ID de sesión
        
        return ResponseEntity.ok(responseMap);
        
    } catch (AuthenticationException e) {
            // Log para depuración
            System.out.println("Error de autenticación: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errResponse = new HashMap<>();
            errResponse.put("message", "Credenciales inválidas: " + e.getMessage());
            errResponse.put("error", true);
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    // Método para manejar peticiones con parámetros en lugar de JSON
    @PostMapping("/login/form")
    public ResponseEntity<?> authenticateUserForm(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response) {
                
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        
        return authenticateUser(loginRequest, request, response);
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

    @GetMapping("/check")
    public ResponseEntity<?> checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Log para depuración
        System.out.println("Verificando autenticación para: " + 
                        (authentication != null ? authentication.getName() : "No hay autenticación"));
        
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication instanceof AnonymousAuthenticationToken)) {
            
            // Extrae información del usuario autenticado
            List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("authenticated", true);
            response.put("username", authentication.getName());
            response.put("roles", roles);
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("authenticated", false, "message", "No autenticado"));
        }
    }
}