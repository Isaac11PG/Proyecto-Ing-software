package com.holamundo.HOLASPRING6CV3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.holamundo.HOLASPRING6CV3.services.UsuarioService;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String mostrarPaginaRegistro(Model model) {
        System.out.println("Accediendo a página de registro (GET)");
        return "registro";  // Esto busca la plantilla llamada registro.html
    }

    @PostMapping("/registro")
    public String registrarUsuario(
            @RequestParam("usuario") String usuario, 
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {
        
        System.out.println("Procesando formulario de registro (POST)");
        System.out.println("Datos recibidos - Usuario: " + usuario + ", Email: " + email);
        
        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            model.addAttribute("mensaje", "Las contraseñas no coinciden");
            model.addAttribute("error", true);
            model.addAttribute("usuarioForm", usuario);
            model.addAttribute("emailForm", email);
            return "registro";
        }
        
        try {
            // Lógica de registro de usuario
            boolean registrado = usuarioService.registrarUsuario(usuario, email, password);
            
            System.out.println("Resultado del registro: " + (registrado ? "ÉXITO" : "FALLIDO"));
            
            if (registrado) {
                model.addAttribute("mensaje", "Registro exitoso. Ahora puedes iniciar sesión.");
                return "login";  // Ir directamente a la página de login con el mensaje
            } else {
                model.addAttribute("mensaje", "Error al registrar el usuario. Es posible que el nombre de usuario ya exista.");
                model.addAttribute("error", true);
                model.addAttribute("usuarioForm", usuario);
                model.addAttribute("emailForm", email);
                return "registro";
            }
        } catch (Exception e) {
            System.err.println("Excepción en el controlador de registro: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("mensaje", "Error técnico al procesar el registro: " + e.getMessage());
            model.addAttribute("error", true);
            model.addAttribute("usuarioForm", usuario);
            model.addAttribute("emailForm", email);
            return "registro";
        }
    }
}