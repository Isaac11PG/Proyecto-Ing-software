package com.holamundo.HOLASPRING6CV3.controllers;

import com.holamundo.HOLASPRING6CV3.models.SismoModel;
import com.holamundo.HOLASPRING6CV3.services.SismoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/sismos")
public class SismoController {

    private final SismoService sismoService;

    @Autowired
    public SismoController(SismoService sismoService) {
        this.sismoService = sismoService;
    }

    @GetMapping
    public String paginaSismos(Model model) {
        model.addAttribute("sismos", sismoService.obtenerTodos());
        return "sismos/index";
    }

    @GetMapping("/subir")
    public String mostrarFormularioSubida() {
        return "sismos/subir";
    }

    @PostMapping("/subir")
    public String subirArchivo(@RequestParam("archivo") MultipartFile archivo,
                              RedirectAttributes redirectAttributes) {
        if (archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "Por favor selecciona un archivo para subir");
            return "redirect:/sismos/subir";
        }

        try {
            List<SismoModel> sismos = sismoService.procesarArchivoCSV(archivo);
            redirectAttributes.addFlashAttribute("mensaje", 
                "¡Archivo subido correctamente! Se procesaron " + sismos.size() + " registros.");
            return "redirect:/sismos";
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensaje", 
                "Error al procesar el archivo: " + e.getMessage());
            return "redirect:/sismos/subir";
        }
    }
    @GetMapping("/mapa")
    public String mostrarMapa() {
        return "sismos/mapa";
    }

    @GetMapping("/api/data")
    @ResponseBody
    public List<SismoModel> obtenerDatosSismos() {
        return sismoService.obtenerTodos();
    }

}