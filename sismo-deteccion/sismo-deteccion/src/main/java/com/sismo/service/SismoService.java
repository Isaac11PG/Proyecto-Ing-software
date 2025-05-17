package com.sismo.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sismo.model.Sismo;
import com.sismo.repository.SismoRepository;

import jakarta.transaction.Transactional;

@Service
public class SismoService {
    
    private static final Logger logger = LoggerFactory.getLogger(SismoService.class);
    private final SismoRepository sismoRepository;
    
    @Value("${spring.servlet.multipart.location:${java.io.tmpdir}}")
    private String uploadDir;

    public SismoService(SismoRepository sismoRepository) {
        this.sismoRepository = sismoRepository;
    }

    // Métodos básicos de CRUD
    public List<Sismo> obtenerTodos() {
        return sismoRepository.findAll();
    }
    
    public Optional<Sismo> obtenerPorId(Long id) {
        return sismoRepository.findById(id);
    }
    
    public List<Sismo> obtenerPorMagnitud(double magnitudMinima) {
        return sismoRepository.findByMagnitudGreaterThan(magnitudMinima);
    }
    
    @Transactional
    public Sismo guardar(Sismo sismo) {
        return sismoRepository.save(sismo);
    }
    
    @Transactional
    public List<Sismo> guardarLote(List<Sismo> sismos) {
        return sismoRepository.saveAll(sismos);
    }
    
    @Transactional
    public void eliminar(Long id) {
        sismoRepository.deleteById(id);
    }
    
    public long contarSismos() {
        return sismoRepository.count();
    }

    // Métodos para procesamiento de archivos CSV
    @Transactional
    public void cargarSismosDesdeCSV(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            logger.error("Archivo no proporcionado o vacío");
            throw new IllegalArgumentException("Archivo no proporcionado o vacío");
        }
        
        logger.info("Iniciando procesamiento del archivo CSV: {}, tamaño: {}", 
                   file.getOriginalFilename(), file.getSize());
        
        // Crear un directorio temporal si no existe
        File tempDir = new File(uploadDir);
        if (!tempDir.exists()) {
            logger.info("Creando directorio temporal: {}", tempDir.getAbsolutePath());
            if (!tempDir.mkdirs()) {
                logger.warn("No se pudo crear el directorio temporal: {}", tempDir.getAbsolutePath());
            }
        }
        
        // Guardar el archivo en el sistema de archivos
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path targetPath = Path.of(uploadDir, fileName);
        
        logger.info("Guardando archivo en: {}", targetPath);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        // Procesar el archivo guardado
        File csvFile = targetPath.toFile();
        cargarSismosDesdeArchivo(csvFile);
    }
    
    @Transactional
    public void cargarSismosDesdeArchivo(File csvFile) throws IOException {
        if (csvFile == null || !csvFile.exists() || csvFile.length() == 0) {
            logger.error("Archivo no encontrado o vacío: {}", csvFile != null ? csvFile.getAbsolutePath() : "null");
            throw new IllegalArgumentException("Archivo no encontrado o vacío");
        }
        
        logger.info("Procesando archivo CSV: {}, tamaño: {} bytes", csvFile.getName(), csvFile.length());
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8));
             CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<Sismo> sismosBatch = new ArrayList<>();
            int recordCount = 0;
            int batchSize = 500;
            
            logger.info("Procesando registros CSV...");
            for (CSVRecord record : csvParser) {
                try {
                    if (record.size() < 9) {
                        logger.warn("Registro CSV con columnas insuficientes. Línea {}: {}", 
                                csvParser.getCurrentLineNumber(), record);
                        continue;
                    }
                    
                    Sismo sismo = new Sismo();
                    
                    try {
                        sismo.setFecha(parseFecha(record.get("Fecha"), dateFormatter));
                    } catch (Exception e) {
                        logger.warn("Error al procesar fecha en línea {}: {}", 
                                csvParser.getCurrentLineNumber(), e.getMessage());
                    }
                    
                    sismo.setHora(record.get("Hora"));
                    sismo.setMagnitud(parseMagnitud(record.get("Magnitud")));
                    
                    try {
                        sismo.setLatitud(parseDouble(record.get("Latitud"), "Latitud"));
                        sismo.setLongitud(parseDouble(record.get("Longitud"), "Longitud"));
                        sismo.setProfundidad(parseDouble(record.get("Profundidad"), "Profundidad"));
                    } catch (Exception e) {
                        logger.warn("Error al procesar coordenadas en línea {}: {}", 
                                csvParser.getCurrentLineNumber(), e.getMessage());
                        continue;
                    }
                    
                    sismo.setReferenciaLocalizacion(record.get("Referencia de localizacion"));
                    
                    try {
                        sismo.setFechaUTC(parseFecha(record.get("Fecha UTC"), dateFormatter));
                    } catch (Exception e) {
                        logger.warn("Error al procesar fecha UTC en línea {}: {}", 
                                csvParser.getCurrentLineNumber(), e.getMessage());
                    }
                    
                    sismo.setHoraUTC(record.get("Hora UTC"));
                    sismo.setEstatus(record.get("Estatus"));
                    
                    sismosBatch.add(sismo);
                    recordCount++;

                    if (sismosBatch.size() >= batchSize) {
                        sismoRepository.saveAll(sismosBatch);
                        logger.info("Guardados {} registros en la base de datos (total: {})", 
                                sismosBatch.size(), recordCount);
                        sismosBatch.clear();
                    }
                } catch (Exception e) {
                    logger.error("Error procesando registro CSV en línea {}: {}", 
                            csvParser.getCurrentLineNumber(), e.getMessage());
                }
            }

            if (!sismosBatch.isEmpty()) {
                sismoRepository.saveAll(sismosBatch);
                logger.info("Guardados {} registros restantes en la base de datos (total: {})", 
                        sismosBatch.size(), recordCount);
            }
            
            logger.info("Procesamiento CSV completado. Total registros procesados: {}", recordCount);
        } catch (Exception e) {
            logger.error("Error grave procesando el archivo CSV", e);
            throw new IOException("Error procesando el archivo CSV: " + e.getMessage(), e);
        }
    }

    // Métodos auxiliares para parseo de datos
    private Double parseMagnitud(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("no calculable")) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.warn("Error al parsear magnitud '{}': {}", value, e.getMessage());
            return null;
        }
    }
    
    private Double parseDouble(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            logger.warn("Valor de {} vacío o nulo", field);
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.warn("Error al parsear {} '{}': {}", field, value, e.getMessage());
            return 0.0;
        }
    }
    
    private LocalDate parseFecha(String value, DateTimeFormatter formatter) {
        if (value == null || value.trim().isEmpty()) {
            logger.warn("Fecha vacía o nula");
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(value, formatter);
        } catch (DateTimeParseException e) {
            logger.warn("Error al parsear fecha '{}': {}", value, e.getMessage());
            return LocalDate.now();
        }
    }
}