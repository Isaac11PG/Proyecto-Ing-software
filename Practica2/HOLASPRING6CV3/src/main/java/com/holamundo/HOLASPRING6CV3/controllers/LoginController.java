package com.holamundo.HOLASPRING6CV3.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String mostrarPaginaLogin(Model model) {
        model.addAttribute("mensaje", "Bienvenido al sistema. Inicia sesión.");
        return "login"; // Esto busca una plantilla llamada login.html
    }
}

