package com.sismo.service;

import java.io.BufferedReader;
<<<<<<< Updated upstream
import java.io.ByteArrayInputStream;
=======
import java.io.File;
import java.io.FileInputStream;
>>>>>>> Stashed changes
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
<<<<<<< Updated upstream
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
=======
import java.util.UUID;
>>>>>>> Stashed changes

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sismo.model.Sismo;
import com.sismo.repository.SismoRepository;

import jakarta.transaction.Transactional;

@Service
public class SismoService {
<<<<<<< Updated upstream
    private static final Logger logger = LoggerFactory.getLogger(SismoService.class);
    private final SismoRepository sismoRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
=======
    
    private static final Logger logger = LoggerFactory.getLogger(SismoService.class);
    private final SismoRepository sismoRepository;
    
    @Value("${spring.servlet.multipart.location:${java.io.tmpdir}}")
    private String uploadDir;
>>>>>>> Stashed changes

    public SismoService(SismoRepository sismoRepository) {
        this.sismoRepository = sismoRepository;
    }

    // Método para obtener todos los sismos
    public List<Sismo> obtenerTodos() {
        return sismoRepository.findAll();
    }

    // Método para filtrar sismos por magnitud
    public List<Sismo> obtenerPorMagnitud(double magnitud) {
        return sismoRepository.findByMagnitudGreaterThan(magnitud);
    }

    /**
     * Método para cargar sismos desde un archivo MultipartFile
     * Este método guarda primero el archivo y luego llama al método que procesa el archivo
     */
    @Transactional
<<<<<<< Updated upstream
public void cargarSismosDesdeCSV(byte[] fileContent, String filename) throws IOException {
    logger.info("Iniciando procesamiento de archivo CSV: {}", filename);
    List<Sismo> sismos = new ArrayList<>();
    
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new ByteArrayInputStream(fileContent), StandardCharsets.UTF_8))) {
        
        boolean headerProcessed = false;
        String line;
        int lineNum = 0;
        
        while ((line = reader.readLine()) != null) {
            lineNum++;
            
            // Ignorar líneas de comentarios, vacías o metadatos
            if (line.trim().isEmpty() || line.startsWith("\"") || line.contains("Catalogo") 
                || line.contains("Informacion") || line.contains("Sismicidad")) {
                logger.debug("Ignorando línea {}: {}", lineNum, line);
                continue;
            }
            
            // Verificar si es la línea de encabezados
            if (!headerProcessed && line.contains("Fecha,Hora,Magnitud")) {
                headerProcessed = true;
                logger.debug("Ignorando encabezados en línea {}: {}", lineNum, line);
                continue;
            }
            
            // Procesar línea de datos
            try {
                Sismo sismo = procesarLineaSismo(line);
                if (sismo != null) {
                    sismos.add(sismo);
                }
            } catch (Exception e) {
                logger.error("Error al procesar línea {}: {}", lineNum, e.getMessage());
            }
        }
        
        logger.info("Procesadas {} líneas, encontrados {} registros válidos", lineNum, sismos.size());
        
        // Guardar todos los sismos en la base de datos
        if (!sismos.isEmpty()) {
            List<Sismo> saved = sismoRepository.saveAll(sismos);
            logger.info("Guardados {} registros de sismos en la base de datos", saved.size());
            logger.info("IDs de registros guardados: {}", saved.stream().map(Sismo::getId).collect(Collectors.toList()));
        }
    } catch (IOException e) {
        logger.error("Error al leer el archivo CSV: {}", e.getMessage());
        throw e;
    }
}
    
    /**
     * Procesa una línea de datos CSV y la convierte en un objeto Sismo
     */
    private Sismo procesarLineaSismo(String line) {
        // Dividir la línea por comas, teniendo en cuenta las comillas
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        
        if (parts.length < 10) {
            logger.warn("Línea con formato incorrecto, se esperaban al menos 10 campos: {}", line);
            return null;
        }
        
        try {
            Sismo sismo = new Sismo();
            
            // Fecha y hora local
            sismo.setFecha(parseDate(parts[0]));
            sismo.setHora(parts[1]);
            
            // Magnitud
            sismo.setMagnitud(parseMagnitud(parts[2]));
            
            // Coordenadas
            sismo.setLatitud(parseDouble(parts[3]));
            sismo.setLongitud(parseDouble(parts[4]));
            
            // Profundidad
            sismo.setProfundidad(parseDouble(parts[5]));
            
            // Referencia de localización (quitar comillas si las hay)
            String referencia = parts[6];
            if (referencia.startsWith("\"") && referencia.endsWith("\"")) {
                referencia = referencia.substring(1, referencia.length() - 1);
            }
            sismo.setReferenciaLocalizacion(referencia);
            
            // Fecha y hora UTC
            sismo.setFechaUTC(parseDate(parts[7]));
            sismo.setHoraUTC(parts[8]);
            
            // Estatus
            sismo.setEstatus(parts[9]);
            
            return sismo;
        } catch (Exception e) {
            logger.error("Error al procesar línea de sismo: {}", e.getMessage());
            return null;
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.error("Error al parsear fecha '{}': {}", dateStr, e.getMessage());
            return null;
        }
    }

    private Double parseDouble(String value) {
        try {
            return value != null && !value.trim().isEmpty() ? Double.parseDouble(value) : null;
        } catch (NumberFormatException e) {
            logger.error("Error al parsear número '{}': {}", value, e.getMessage());
            return null;
=======
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
    
    /**
     * Método para cargar sismos desde un archivo File del sistema
     * Este método es llamado tanto por el método anterior como directamente por el controlador en procesamiento asíncrono
     */
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
                    if (record.size() < 9) { // Verifica si hay suficientes columnas
                        logger.warn("Registro CSV con columnas insuficientes. Línea {}: {}", 
                                csvParser.getCurrentLineNumber(), record);
                        continue;
                    }
                    
                    Sismo sismo = new Sismo();
                    
                    // Procesamos cada campo con manejo de errores
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
                        continue; // Saltamos este registro si hay problemas con las coordenadas
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

                    // Guardar en lotes para mejor rendimiento
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

            // Guardar los registros restantes
            if (!sismosBatch.isEmpty()) {
                sismoRepository.saveAll(sismosBatch);
                logger.info("Guardados {} registros restantes en la base de datos (total: {})", 
                        sismosBatch.size(), recordCount);
            }
            
            logger.info("Procesamiento CSV completado. Total registros procesados: {}", recordCount);
        } catch (Exception e) {
            logger.error("Error grave procesando el archivo CSV", e);
            throw new IOException("Error procesando el archivo CSV: " + e.getMessage(), e);
>>>>>>> Stashed changes
        }
    }

    private Double parseMagnitud(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("no calculable")) {
            return null; // Guarda NULL en la base de datos
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
<<<<<<< Updated upstream
            logger.error("Error al parsear magnitud '{}': {}", value, e.getMessage());
            return null;
        }
    }
=======
            logger.warn("Error al parsear magnitud '{}': {}", value, e.getMessage());
            return null;
        }
    }
    
    private Double parseDouble(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            logger.warn("Valor de {} vacío o nulo", field);
            return 0.0; // Valor por defecto
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.warn("Error al parsear {} '{}': {}", field, value, e.getMessage());
            return 0.0; // Valor por defecto
        }
    }
    
    private LocalDate parseFecha(String value, DateTimeFormatter formatter) {
        if (value == null || value.trim().isEmpty()) {
            logger.warn("Fecha vacía o nula");
            return LocalDate.now(); // Valor por defecto
        }
        try {
            return LocalDate.parse(value, formatter);
        } catch (DateTimeParseException e) {
            logger.warn("Error al parsear fecha '{}': {}", value, e.getMessage());
            return LocalDate.now(); // Valor por defecto
        }
    }
>>>>>>> Stashed changes
}