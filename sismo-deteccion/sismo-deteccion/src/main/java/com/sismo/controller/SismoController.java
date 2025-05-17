package com.sismo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.sismo.model.Sismo;
import com.sismo.service.SismoService;
import com.sismo.service.SismoServiceFix;
import com.sismo.util.CSVValidator;

@RestController
@RequestMapping("/api/sismos")
public class SismoController {
    private static final Logger logger = LoggerFactory.getLogger(SismoController.class);
    private final SismoService sismoService;
    private final CSVValidator csvValidator;
    
    // Añadir el servicio de diagnóstico/corrección
    @Autowired
    private SismoServiceFix sismoServiceFix;
    
    @Value("${spring.servlet.multipart.location:${java.io.tmpdir}}")
    private String uploadDir;

    public SismoController(SismoService sismoService, CSVValidator csvValidator) {
        this.sismoService = sismoService;
        this.csvValidator = csvValidator;
    }

    // Endpoint para obtener todos los sismos
    @GetMapping
    public List<Sismo> obtenerTodos() {
        logger.info("Solicitando todos los sismos");
        return sismoService.obtenerTodos();
    }

    // Endpoint para obtener sismos por magnitud mínima
    @GetMapping("/magnitud/{minMagnitud}")
    public List<Sismo> obtenerPorMagnitud(@PathVariable double minMagnitud) {
        logger.info("Solicitando sismos con magnitud mayor a {}", minMagnitud);
        return sismoService.obtenerPorMagnitud(minMagnitud);
    }

    // Endpoint para diagnóstico
    @GetMapping("/diagnostico")
    public ResponseEntity<String> ejecutarDiagnostico() {
        logger.info("Ejecutando diagnóstico de la base de datos");
        try {
            sismoServiceFix.diagnosticarBaseDeDatos();
            return ResponseEntity.ok("Diagnóstico completado. Revise los logs para más detalles.");
        } catch (Exception e) {
            logger.error("Error en diagnóstico", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en diagnóstico: " + e.getMessage());
        }
    }
    
    // Endpoint para verificar última subida
    @GetMapping("/verificar-ultima")
    public ResponseEntity<String> verificarUltimaCarga() {
        logger.info("Verificando última carga de datos");
        try {
            sismoServiceFix.verificarUltimaSubida();
            return ResponseEntity.ok("Verificación completada. Revise los logs para más detalles.");
        } catch (Exception e) {
            logger.error("Error en verificación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en verificación: " + e.getMessage());
        }
    }

    // Endpoint para cargar datos desde un CSV (CORREGIDO)
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/cargar")
    public ResponseEntity<String> cargarDesdeCSV(@RequestParam("file") MultipartFile file) {
        logger.info("Recibida solicitud para cargar archivo CSV: {}, tipo: {}, tamaño: {}",
                file.getOriginalFilename(), file.getContentType(), file.getSize());
        
        if (file.isEmpty()) {
            logger.warn("Archivo vacío recibido");
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }
        
        // Verificar extensión de archivo
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            logger.warn("Tipo de archivo no soportado: {}", filename);
            return ResponseEntity.badRequest().body("Solo se permiten archivos CSV");
        }
        
        try {
            // Validar estructura del CSV antes de procesar
            if (!csvValidator.validateCSVStructure(file)) {
                logger.warn("Estructura del CSV inválida");
                return ResponseEntity.badRequest().body("El archivo CSV no tiene la estructura esperada");
            }
            
            // Crear directorio temporal si no existe
            File tempDir = new File(uploadDir);
            if (!tempDir.exists()) {
                logger.info("Creando directorio temporal: {}", tempDir.getAbsolutePath());
                if (!tempDir.mkdirs()) {
                    logger.warn("No se pudo crear el directorio temporal: {}", tempDir.getAbsolutePath());
                }
            }
            
            // Guardar el archivo en el sistema de archivos
            String tempFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path tempFilePath = Path.of(uploadDir, tempFileName);
            
            logger.info("Guardando archivo temporal en: {}", tempFilePath);
            Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            
            File csvFile = tempFilePath.toFile();
            
            // MODIFICACIÓN: Procesamiento síncrono con versión corregida
            try {
                logger.info("Iniciando procesamiento síncrono mejorado del CSV");
                sismoServiceFix.cargarSismosDesdeArchivoConFix(csvFile);
                logger.info("Procesamiento directo del CSV completado");
                
                // Verificación inmediata
                sismoServiceFix.verificarUltimaSubida();
                
                // Eliminar archivo temporal
                try {
                    Files.deleteIfExists(tempFilePath);
                    logger.info("Archivo temporal eliminado: {}", tempFilePath);
                } catch (IOException e) {
                    logger.warn("No se pudo eliminar el archivo temporal: {}", tempFilePath, e);
                }
                
                return ResponseEntity.ok("Archivo procesado correctamente. Datos insertados en la base de datos.");
            } catch (Exception e) {
                logger.error("Error al procesar el archivo CSV", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al procesar el archivo: " + e.getMessage());
            }
        } catch (IOException e) {
            logger.error("Error al acceder al contenido del archivo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el archivo: " + e.getMessage());
        }
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException e) {
        logger.error("Tamaño de archivo excedido", e);
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body("El archivo excede el tamaño máximo permitido de 50MB");
    }
}