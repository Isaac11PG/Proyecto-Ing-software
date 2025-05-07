package com.sismo.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sismo.model.Sismo;
import com.sismo.repository.SismoRepository;

/**
 * Componente para verificar y corregir problemas con la base de datos
 * Ejecútalo como un bean al iniciar la aplicación
 */
@Component
public class SismoDbFix implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SismoDbFix.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private SismoRepository sismoRepository;
    
    @Override
    public void run(String... args) throws Exception {
        checkAndFixDatabaseStructure();
        checkExistingData();
    }
    
    /**
     * Verifica la estructura de la tabla de sismos y la corrige si es necesario
     */
    private void checkAndFixDatabaseStructure() {
        logger.info("Verificando estructura de la base de datos...");
        
        try {
            // Verificar si la tabla sismos existe
            Integer tablesCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'sismos'", 
                Integer.class);
            
            if (tablesCount == null || tablesCount == 0) {
                logger.warn("¡La tabla 'sismos' no existe! Creándola...");
                createSismosTable();
            } else {
                logger.info("Tabla 'sismos' encontrada. Verificando estructura...");
                checkTableStructure();
            }
            
            // Verificar el auto_increment
            verifyAutoIncrement();
            
        } catch (Exception e) {
            logger.error("Error al verificar la estructura de la base de datos", e);
        }
    }
    
    /**
     * Crea la tabla sismos con la estructura correcta
     */
    private void createSismosTable() {
        try {
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS sismos (
                  id bigint NOT NULL AUTO_INCREMENT,
                  fecha date DEFAULT NULL,
                  hora varchar(255) DEFAULT NULL,
                  magnitud double DEFAULT NULL,
                  latitud double NOT NULL,
                  longitud double NOT NULL,
                  profundidad double NOT NULL,
                  referencia_localizacion varchar(255) DEFAULT NULL,
                  fecha_utc date DEFAULT NULL,
                  hora_utc varchar(255) DEFAULT NULL,
                  estatus varchar(255) DEFAULT NULL,
                  PRIMARY KEY (id),
                  KEY idx_sismos_magnitud (magnitud),
                  KEY idx_sismos_fecha (fecha),
                  KEY idx_sismos_ubicacion (latitud,longitud)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """);
            logger.info("Tabla 'sismos' creada exitosamente");
        } catch (Exception e) {
            logger.error("Error al crear la tabla 'sismos'", e);
        }
    }
    
    /**
     * Verifica la estructura de la tabla sismos
     */
    private void checkTableStructure() {
        try {
            // Verificar columnas
            List<String> columns = jdbcTemplate.queryForList(
                "SHOW COLUMNS FROM sismos", String.class);
            
            logger.info("Columnas encontradas en la tabla 'sismos': {}", columns);
            
            // Verificar tipo de motor (debe ser InnoDB para soportar transacciones)
            String engine = jdbcTemplate.queryForObject(
                "SELECT ENGINE FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'sismos'",
                String.class);
            
            logger.info("Motor de la tabla 'sismos': {}", engine);
            
            if (!"InnoDB".equalsIgnoreCase(engine)) {
                logger.warn("¡La tabla 'sismos' no usa el motor InnoDB! Cambiando...");
                jdbcTemplate.execute("ALTER TABLE sismos ENGINE = InnoDB");
                logger.info("Motor de tabla cambiado a InnoDB");
            }
        } catch (Exception e) {
            logger.error("Error al verificar la estructura de la tabla", e);
        }
    }
    
    /**
     * Verifica que la columna id tenga AUTO_INCREMENT configurado
     */
    private void verifyAutoIncrement() {
        try {
            List<String> columnDef = jdbcTemplate.queryForList(
                "SHOW COLUMNS FROM sismos WHERE Field = 'id'", 
                String.class);
            
            logger.info("Definición de la columna 'id': {}", columnDef);
            
            // Verificar que la columna tenga AUTO_INCREMENT
            String columnInfo = jdbcTemplate.queryForObject(
                "SELECT COLUMN_TYPE, EXTRA FROM information_schema.columns " +
                "WHERE table_schema = DATABASE() AND table_name = 'sismos' AND column_name = 'id'",
                String.class);
            
            logger.info("Información de la columna 'id': {}", columnInfo);
            
            if (columnInfo == null || !columnInfo.contains("auto_increment")) {
                logger.warn("¡La columna 'id' no tiene AUTO_INCREMENT! Corrigiendo...");
                jdbcTemplate.execute("ALTER TABLE sismos MODIFY id BIGINT NOT NULL AUTO_INCREMENT");
                logger.info("AUTO_INCREMENT configurado para la columna 'id'");
            }
        } catch (Exception e) {
            logger.error("Error al verificar AUTO_INCREMENT", e);
        }
    }
    
    /**
     * Verifica los datos existentes en la tabla
     */
    private void checkExistingData() {
        try {
            long count = sismoRepository.count();
            logger.info("Total de registros en la tabla 'sismos': {}", count);
            
            if (count > 0) {
                // Mostrar el primer registro (si existe)
                Optional<Sismo> firstSismo = sismoRepository.findAll().stream().findFirst();
                if (firstSismo.isPresent()) {
                    Sismo sismo = firstSismo.get();
                    logger.info("Primer registro: ID={}, Magnitud={}, Fecha={}, Localización={}",
                              sismo.getId(), sismo.getMagnitud(), sismo.getFecha(), 
                              sismo.getReferenciaLocalizacion());
                }
            }
        } catch (Exception e) {
            logger.error("Error al verificar datos existentes", e);
        }
    }
}