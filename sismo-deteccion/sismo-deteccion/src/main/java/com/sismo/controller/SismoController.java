package com.sismo.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sismo.model.Sismo;
import com.sismo.service.SismoService;

@RestController
@RequestMapping("/api/sismos")
public class SismoController {
    private final SismoService sismoService;

    public SismoController(SismoService sismoService) {
        this.sismoService = sismoService;
    }

    // Endpoint para obtener todos los sismos
    @GetMapping
    public List<Sismo> obtenerTodos() {
        return sismoService.obtenerTodos();
    }

    // Endpoint para obtener sismos por magnitud mínima
    @GetMapping("/magnitud/{minMagnitud}")
    public List<Sismo> obtenerPorMagnitud(@PathVariable double minMagnitud) {
        return sismoService.obtenerPorMagnitud(minMagnitud);
    }

    // Endpoint para cargar datos desde un CSV
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/cargar")
    public ResponseEntity<String> cargarDesdeCSV(@RequestParam("file") MultipartFile file) {
        CompletableFuture.runAsync(() -> {
            try {
                sismoService.cargarSismosDesdeCSV(file);
            } catch (IOException e) {
                System.out.println("Error procesando CSV: " + e.getMessage());
            }
        });
        
        return ResponseEntity.accepted().body("Archivo recibido. Procesamiento iniciado en segundo plano.");
    }
}

