package com.sismo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CSVValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(CSVValidator.class);
    
    // Lista de encabezados esperados
    private static final List<String> EXPECTED_HEADERS = Arrays.asList(
            "Fecha", "Hora", "Magnitud", "Latitud", "Longitud", 
            "Profundidad", "Referencia de localizacion", "Fecha UTC", "Hora UTC", "Estatus");
    
    /**
     * Valida que el archivo CSV tenga la estructura esperada
     */
    public boolean validateCSVStructure(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("Archivo CSV vacío o nulo");
            return false;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {
            
            // Obtener los encabezados del CSV
            Set<String> headers = csvParser.getHeaderMap().keySet();
            logger.info("Encabezados encontrados en el CSV: {}", headers);
            
            // Normalizar y verificar encabezados ignorando mayúsculas/minúsculas y espacios
            boolean headersValid = validateHeaders(headers);
            
            // Verificar que tenga al menos una fila de datos
            boolean hasData = csvParser.iterator().hasNext();
            if (!hasData) {
                logger.warn("El CSV no contiene datos");
                return false;
            }
            
            return headersValid;
        } catch (IOException e) {
            logger.error("Error al validar estructura del CSV", e);
            return false;
        }
    }
    
    /**
     * Verifica que los encabezados del CSV coincidan con los esperados
     * (ignorando diferencias en mayúsculas/minúsculas y espacios extra)
     */
    private boolean validateHeaders(Set<String> foundHeaders) {
        // Convertir los encabezados a formato normalizado para comparación
        Set<String> normalizedFoundHeaders = normalizeHeaders(foundHeaders);
        Set<String> normalizedExpectedHeaders = normalizeHeaders(new HashSet<>(EXPECTED_HEADERS));
        
        // Verificar si todos los encabezados esperados están presentes
        boolean allExpectedHeadersPresent = normalizedFoundHeaders.containsAll(normalizedExpectedHeaders);
        
        if (!allExpectedHeadersPresent) {
            Set<String> missingHeaders = new HashSet<>(normalizedExpectedHeaders);
            missingHeaders.removeAll(normalizedFoundHeaders);
            logger.warn("Faltan encabezados esperados: {}", missingHeaders);
        }
        
        // Verificar si hay encabezados adicionales (opcional, solo informativo)
        Set<String> extraHeaders = new HashSet<>(normalizedFoundHeaders);
        extraHeaders.removeAll(normalizedExpectedHeaders);
        if (!extraHeaders.isEmpty()) {
            logger.info("Encabezados adicionales encontrados: {}", extraHeaders);
        }
        
        // Verificar que todas las columnas críticas estén presentes
        boolean criticalHeadersPresent = normalizedFoundHeaders.containsAll(
                normalizeHeaders(new HashSet<>(Arrays.asList(
                        "Fecha", "Magnitud", "Latitud", "Longitud", "Profundidad")))
        );
        
        if (!criticalHeadersPresent) {
            logger.warn("Faltan encabezados críticos para el procesamiento del CSV");
            return false;
        }
        
        logger.info("Validación de encabezados: {}. Se encontraron {}/{} encabezados esperados.",
                allExpectedHeadersPresent ? "ÉXITO" : "ADVERTENCIA", 
                countMatchingHeaders(normalizedFoundHeaders, normalizedExpectedHeaders),
                normalizedExpectedHeaders.size());
        
        // Permitir procesar incluso con algunos encabezados faltantes siempre que estén los críticos
        return criticalHeadersPresent;
    }
    
    /**
     * Normaliza los encabezados (elimina espacios extra, convierte a minúsculas)
     */
    private Set<String> normalizeHeaders(Set<String> headers) {
        Set<String> normalized = new HashSet<>();
        for (String header : headers) {
            normalized.add(header.trim().toLowerCase());
        }
        return normalized;
    }
    
    /**
     * Cuenta cuántos encabezados esperados se encontraron
     */
    private int countMatchingHeaders(Set<String> found, Set<String> expected) {
        int count = 0;
        for (String header : expected) {
            if (found.contains(header)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Extrae una muestra de datos del CSV para diagnóstico
     */
    public String extractSampleData(MultipartFile file, int numRows) {
        if (file == null || file.isEmpty()) {
            return "Archivo vacío";
        }
        
        StringBuilder sample = new StringBuilder("Muestra de datos:\n");
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {
            
            int count = 0;
            for (var record : csvParser) {
                if (count >= numRows) break;
                
                sample.append("Fila ").append(count + 1).append(": ");
                for (String header : EXPECTED_HEADERS) {
                    if (record.isMapped(header)) {
                        sample.append(header).append("=").append(record.get(header)).append(", ");
                    }
                }
                sample.append("\n");
                count++;
            }
            
            return sample.toString();
            
        } catch (IOException e) {
            logger.error("Error al extraer muestra de datos", e);
            return "Error al extraer muestra: " + e.getMessage();
        }
    }
}