package com.holamundo.HOLASPRING6CV3.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class CustomErrorController {

    @RequestMapping("/error")
    public String handleError(Model model) {
        // Puedes agregar información adicional a la vista
        model.addAttribute("message", "Ha ocurrido un error inesperado.");
        return "error"; // Nombre de la vista o página HTML a mostrar
    }
}
