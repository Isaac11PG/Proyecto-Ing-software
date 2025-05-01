package com.holamundo.HOLASPRING6CV3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.holamundo.HOLASPRING6CV3.services.UsuarioService;

@Controller
public class ThemeController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/cambiar-tema")
    public String cambiarTema(@RequestParam("theme") String theme, 
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        boolean actualizado = usuarioService.actualizarTema(username, theme);
        
        if (actualizado) {
            redirectAttributes.addFlashAttribute("mensaje", "Tema actualizado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el tema");
        }
        
        // Redirigir a la página donde se solicitó el cambio
        return "redirect:" + redirectAttributes.getAttribute("referer");
    }
}