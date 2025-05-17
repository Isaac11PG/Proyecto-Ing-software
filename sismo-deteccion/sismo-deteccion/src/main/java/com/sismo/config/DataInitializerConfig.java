package com.sismo.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.sismo.model.Sismo;
import com.sismo.service.GrafoService;
import com.sismo.service.SismoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DataInitializerConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializerConfig.class);
    
    @Autowired
    private Environment env;
    
    @Bean
    public CommandLineRunner initData(SismoService sismoService, GrafoService grafoService) {
        return args -> {
            try {
                logger.info("Iniciando carga de datos desde CSV a la base de datos y grafo Neo4j");
                
                // Verificar si existen sismos en la base de datos
                long sismoCount = sismoService.contarSismos();
                if (sismoCount > 0) {
                    logger.info("La base de datos ya tiene {} sismos. Omitiendo carga inicial.", sismoCount);
                    return;
                }
                
                // Determinar la ruta del archivo CSV
                String csvPath = "/imports/sismos.csv";
                File csvFile = new File(csvPath);
                
                if (!csvFile.exists()) {
                    logger.warn("Archivo CSV no encontrado en: {}. Intentando con otras rutas...", csvPath);
                    
                    // Intentar con rutas alternativas
                    String[] alternativePaths = {
                        "./imports/sismos.csv",
                        "../imports/sismos.csv",
                        "/app/imports/sismos.csv"
                    };
                    
                    for (String path : alternativePaths) {
                        csvFile = new File(path);
                        if (csvFile.exists()) {
                            csvPath = path;
                            logger.info("CSV encontrado en ruta alternativa: {}", csvPath);
                            break;
                        }
                    }
                }
                
                if (!csvFile.exists()) {
                    logger.error("No se pudo encontrar el archivo CSV de sismos en ninguna ruta. Abortando carga inicial.");
                    return;
                }
                
                // Procesar el archivo CSV
                List<Sismo> sismos = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                
                try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                    // Saltar la primera línea (encabezados)
                    String line = br.readLine();
                    
                    // Leer los datos
                    while ((line = br.readLine()) != null) {
                        String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                        
                        if (fields.length < 10) {
                            logger.warn("Línea con formato incorrecto: {}", line);
                            continue;
                        }
                        
                        try {
                            Sismo sismo = new Sismo();
                            sismo.setFecha(LocalDate.parse(fields[0].trim(), formatter));
                            sismo.setHora(fields[1].trim());
                            sismo.setMagnitud(Double.parseDouble(fields[2].trim()));
                            sismo.setLatitud(Double.parseDouble(fields[3].trim()));
                            sismo.setLongitud(Double.parseDouble(fields[4].trim()));
                            sismo.setProfundidad(Double.parseDouble(fields[5].trim()));
                            
                            // Limpiar comillas de la referencia de localización
                            String refLocalizacion = fields[6].trim();
                            if (refLocalizacion.startsWith("\"") && refLocalizacion.endsWith("\"")) {
                                refLocalizacion = refLocalizacion.substring(1, refLocalizacion.length() - 1);
                            }
                            sismo.setReferenciaLocalizacion(refLocalizacion);
                            
                            sismo.setFechaUTC(LocalDate.parse(fields[7].trim(), formatter));
                            sismo.setHoraUTC(fields[8].trim());
                            sismo.setEstatus(fields[9].trim());
                            sismo.setCodigo(UUID.randomUUID().toString());
                            sismos.add(sismo);
                        } catch (Exception e) {
                            logger.error("Error al procesar la línea: {}. Error: {}", line, e.getMessage());
                        }
                    }
                }
                
                if (!sismos.isEmpty()) {
                    logger.info("Cargando {} sismos desde CSV a la base de datos", sismos.size());
                    sismoService.guardarLote(sismos);
                    
                    logger.info("Cargando sismos a Neo4j");
                    grafoService.cargarSismosAGrafo(sismos);
                    
                    logger.info("Carga inicial de datos completada con éxito");
                } else {
                    logger.warn("No se encontraron datos válidos en el CSV");
                }
                
            } catch (Exception e) {
                logger.error("Error durante la carga inicial de datos: {}", e.getMessage(), e);
            }
        };
    }
}