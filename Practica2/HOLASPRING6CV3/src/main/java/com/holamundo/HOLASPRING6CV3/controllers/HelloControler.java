package com.holamundo.HOLASPRING6CV3.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloControler{

    @GetMapping("/hola")
    public String hello(Model model) {
        model.addAttribute("mensaje", "¡Hola Spring!");
        return "hola"; // Devuelve el nombre de la plantilla Thymeleaf (hola.html)
    }
}
