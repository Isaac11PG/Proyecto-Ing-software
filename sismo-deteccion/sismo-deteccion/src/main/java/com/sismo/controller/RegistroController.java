package com.sismo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sismo.service.UsuarioService;

@RestController
@RequestMapping("/api/registro")
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String mostrarPaginaRegistro() {
        System.out.println("Accediendo a página de registro (GET)");
        return "Accediendo a página de registro (GET)";
    }

    @PostMapping
    public RegistroResponse registrarUsuario(
            @RequestParam("usuario") String usuario, 
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword) {
        
        System.out.println("Procesando formulario de registro (POST)");
        System.out.println("Datos recibidos - Usuario: " + usuario + ", Email: " + email);
        
        RegistroResponse response = new RegistroResponse();
        
        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            response.setMensaje("Las contraseñas no coinciden");
            response.setError(true);
            response.setUsuarioForm(usuario);
            response.setEmailForm(email);
            return response;
        }
        
        try {
            // Lógica de registro de usuario
            boolean registrado = usuarioService.registrarUsuario(usuario, email, password);
            
            System.out.println("Resultado del registro: " + (registrado ? "ÉXITO" : "FALLIDO"));
            
            if (registrado) {
                response.setMensaje("Registro exitoso. Ahora puedes iniciar sesión.");
                response.setError(false);
            } else {
                response.setMensaje("Error al registrar el usuario. Es posible que el nombre de usuario ya exista.");
                response.setError(true);
                response.setUsuarioForm(usuario);
                response.setEmailForm(email);
            }
        } catch (Exception e) {
            System.err.println("Excepción en el controlador de registro: " + e.getMessage());
            e.printStackTrace();
            
            response.setMensaje("Error técnico al procesar el registro: " + e.getMessage());
            response.setError(true);
            response.setUsuarioForm(usuario);
            response.setEmailForm(email);
        }
        
        return response;
    }
}

class RegistroResponse {
    private String mensaje;
    private boolean error;
    private String usuarioForm;
    private String emailForm;

    // Getters y setters

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getUsuarioForm() {
        return usuarioForm;
    }

    public void setUsuarioForm(String usuarioForm) {
        this.usuarioForm = usuarioForm;
    }

    public String getEmailForm() {
        return emailForm;
    }

    public void setEmailForm(String emailForm) {
        this.emailForm = emailForm;
    }
}