package com.sismo.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
import com.sismo.repository.SismoRepository;

@RestController
@RequestMapping("/api/sismos")
public class SismoController {
    private static final Logger logger = LoggerFactory.getLogger(SismoController.class);
    private final SismoService sismoService;
    private final SismoRepository sismoRepository;

    public SismoController(SismoService sismoService, SismoRepository sismoRepository) {
        this.sismoService = sismoService;
        this.sismoRepository = sismoRepository;
    }

    @GetMapping("/debug")
    public ResponseEntity<String> debug() {
        long count = sismoRepository.count();
        Sismo first = sismoRepository.findAll().stream().findFirst().orElse(null);
        return ResponseEntity.ok(
            "Total registros: " + count + "\n" +
            "Primer registro: " + (first != null ? first.toString() : "null")
        );
    }

    @GetMapping
    public List<Sismo> obtenerTodos() {
        return sismoService.obtenerTodos();
    }

    @GetMapping("/count")
public ResponseEntity<Long> countSismos() {
    return ResponseEntity.ok(sismoRepository.count());
}

    @GetMapping("/magnitud/{minMagnitud}")
    public List<Sismo> obtenerPorMagnitud(@PathVariable double minMagnitud) {
        return sismoService.obtenerPorMagnitud(minMagnitud);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/cargar")
    public ResponseEntity<String> cargarDesdeCSV(@RequestParam("file") MultipartFile file) {
        logger.info("Inicio de carga de archivo CSV - Nombre: {}, Tamaño: {} bytes", 
                   file.getOriginalFilename(), file.getSize());
        
        if (file.isEmpty()) {
            logger.error("El archivo está vacío");
            return ResponseEntity.badRequest().body("El archivo no puede estar vacío");
        }
        
        // Permitir archivos con extensión .csv incluso si el content-type no es text/csv
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            logger.error("Tipo de archivo no soportado");
            return ResponseEntity.badRequest().body("Solo se permiten archivos CSV");
        }

        try {
            // Leer todo el contenido del archivo a memoria primero
            byte[] fileContent = file.getBytes();
            
            CompletableFuture.runAsync(() -> {
                try {
                    logger.debug("Iniciando procesamiento en segundo plano del archivo: {}", 
                              file.getOriginalFilename());
                    
                    // Pasar el contenido en memoria al servicio
                    sismoService.cargarSismosDesdeCSV(fileContent, file.getOriginalFilename());
                    logger.info("Procesamiento completado exitosamente para: {}", 
                              file.getOriginalFilename());
                } catch (Exception e) {
                    logger.error("Error inesperado al procesar CSV: {}", e.getMessage());
                    logger.debug("Stack trace completo:", e);
                }
            });
            
            logger.info("Archivo aceptado para procesamiento en segundo plano: {}", 
                       file.getOriginalFilename());
            return ResponseEntity.accepted().body("Archivo recibido. Procesamiento iniciado en segundo plano.");
        } catch (IOException e) {
            logger.error("Error al leer el archivo CSV: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Error al procesar el archivo");
        }
    }
}