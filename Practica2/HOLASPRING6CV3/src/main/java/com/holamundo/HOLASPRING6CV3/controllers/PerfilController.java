package com.holamundo.HOLASPRING6CV3.controllers;

import com.holamundo.HOLASPRING6CV3.models.UserModel;
import com.holamundo.HOLASPRING6CV3.repositories.UserRepository;
import com.holamundo.HOLASPRING6CV3.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/perfil")
    public String mostrarPerfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<UserModel> usuario = usuarioService.buscarPorUsername(userDetails.getUsername());

        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
        } else {
            return "redirect:/home?error";  // Redirigir si el usuario no existe
        }

        return "perfil";  // Retorna la vista perfil.html
    }

    @PostMapping("/perfil/actualizar")
public String actualizarPerfil(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam("nombre") String nombre,
        @RequestParam("email") String email) {

    boolean cambiado = usuarioService.actualizarPerfil(userDetails.getUsername(), nombre, email);

    if (cambiado) {
        // Si el nombre de usuario cambió, cerramos la sesión y forzamos un nuevo inicio
        if (!nombre.equals(userDetails.getUsername())) {
            return "redirect:/logout";
        }
        return "redirect:/perfil?success";
    } else {
        return "redirect:/perfil?error";
    }
}

    @PostMapping("/perfil/actualizar-contrasena")
    public String actualizarContraseña(@RequestParam("currentPassword") String currentPassword,
                                        @RequestParam("newPassword") String nuevaPassword,
                                        @RequestParam("confirmPassword") String confirmPassword,
                                        Model model) {
        // Obtener el usuario autenticado actual
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        UserModel usuario = usuarioRepository.findByUsername(username).orElse(null);
                                            
        if (usuario == null) {
            model.addAttribute("mensaje", "Error: No se pudo encontrar el usuario.");
            model.addAttribute("tipoMensaje", "error");
            return "redirect:/perfil?error";
        }
        
        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            model.addAttribute("mensaje", "Error: La contraseña actual es incorrecta.");
            model.addAttribute("tipoMensaje", "error");
            model.addAttribute("usuario", usuario);
            return "redirect:/perfil?error";
        }
        
        // Verificar que las contraseñas nuevas coincidan
        if (!nuevaPassword.equals(confirmPassword)) {
            model.addAttribute("mensaje", "Error: Las contraseñas nuevas no coinciden.");
            model.addAttribute("tipoMensaje", "error");
            model.addAttribute("usuario", usuario);
            return "redirect:/perfil?error";
        }
        
        // Actualizar la contraseña
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
        
        // Añadir mensaje de éxito y datos del usuario al modelo
        model.addAttribute("mensaje", "¡Contraseña actualizada con éxito!");
        model.addAttribute("tipoMensaje", "exito");
        model.addAttribute("usuario", usuario);        
        return "redirect:/perfil?success";
    }

}
