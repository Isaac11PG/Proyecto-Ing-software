package com.sismo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/api/login")
    public String mostrarPaginaLogin() {
        return "Bienvenido al sistema. Inicia sesión.";
    }
}