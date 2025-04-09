package com.holamundo.HOLASPRING6CV3.controllers;

import com.holamundo.HOLASPRING6CV3.models.UserModel;
import com.holamundo.HOLASPRING6CV3.repositories.UserRepository;
import com.holamundo.HOLASPRING6CV3.services.UsuarioService;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    
    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private final UsuarioService usuarioService;

    // Constructor para inyectar el servicio
    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/gestion-usuarios")
    public String verUsuarios(Model model) {
        // Obtener el nombre de usuario logueado
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        // Obtener la lista de usuarios
        List<UserModel> usuarios = usuarioService.obtenerTodosUsuarios();

        // Filtrar para excluir el usuario logueado (tu usuario)
        List<UserModel> usuariosFiltrados = usuarios.stream()
                .filter(usuario -> !usuario.getUsername().equals(username))
                .collect(Collectors.toList());

        // Agregar la lista de usuarios filtrada al modelo
        model.addAttribute("usuarios", usuariosFiltrados);

        return "admin-gestion-usuarios";  // Vista que mostrará la lista sin tu usuario
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/eliminar-usuario")
    public String eliminarUsuario(@RequestParam("id") Long id) {
        usuarioService.eliminarUsuario(id);  // Llamar al servicio para eliminar el usuario
        return "redirect:/admin/gestion-usuarios?success";  // Redirigir con mensaje de éxito
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/actualizar-usuario")
    public String actualizarUsuario(@RequestParam("id") Long id,
                                    @RequestParam("username") String username,
                                    @RequestParam("email") String email,
                                    @RequestParam("rol") String rol) {

        // Actualizar el usuario en el servicio
        boolean actualizado = usuarioService.actualizarUsuarioAdmin(id, username, email, rol);
        // Redirigir según el resultado
        return actualizado ? "redirect:/admin/gestion-usuarios?success" : "redirect:/admin/gestion-usuarios?error";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/actualizar-contrasena")
    public String actualizarContraseña(@RequestParam("password") String password,
                                        @RequestParam("username") String username) {
        System.out.println("Solicitud recibida: Cambiar contraseña para usuario: " + username);

        // Actualizar el usuario en el servicio
        boolean actualizado = usuarioService.actualizarPasswordAdmin(password, username);
        // Redirigir según el resultado
        if (actualizado) {
            System.out.println("Contraseña actualizada correctamente.");
            return "redirect:/admin/gestion-usuarios?success";
        } else {
            System.err.println("Error al actualizar la contraseña para el usuario: " + username);
            return "redirect:/admin/gestion-usuarios?error";
        }    }

}

