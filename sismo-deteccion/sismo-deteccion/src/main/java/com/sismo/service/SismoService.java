package com.sismo.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.sismo.model.Sismo;
import com.sismo.repository.SismoRepository;

import jakarta.transaction.Transactional;

@Service
public class SismoService {
    private static final Logger logger = LoggerFactory.getLogger(SismoService.class);
    private final SismoRepository sismoRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

    @Transactional
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
        }
    }

    private Double parseMagnitud(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("no calculable")) {
            return null; // Guarda NULL en la base de datos
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.error("Error al parsear magnitud '{}': {}", value, e.getMessage());
            return null;
        }
    }
}