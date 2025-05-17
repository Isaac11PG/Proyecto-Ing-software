package com.sismo.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sismo.model.Sismo;
import com.sismo.repository.SismoRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Clase mejorada para depurar problemas con la inserción de datos
 * Se ejecutará automáticamente al iniciar la aplicación
 */
@Component
public class DatabaseDebugger implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseDebugger.class);
    
    @Autowired
    private SismoRepository sismoRepository;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Verificar si hay datos en la tabla
        logger.info("===== INICIANDO DEPURACIÓN DE BASE DE DATOS =====");
        
        // 1.1 Verificar conexión a la base de datos
        try (Connection conn = dataSource.getConnection()) {
            logger.info("Conexión a la base de datos establecida correctamente");
            logger.info("URL: {}", conn.getMetaData().getURL());
            logger.info("Driver: {}", conn.getMetaData().getDriverName());
            logger.info("Usuario: {}", conn.getMetaData().getUserName());
            logger.info("Base de datos: {}", conn.getCatalog());
        } catch (Exception e) {
            logger.error("ERROR al conectar a la base de datos: {}", e.getMessage(), e);
            return;
        }
        
        // 1.2 Verificar estructura de la tabla sismos
        try {
            logger.info("Verificando estructura de la tabla sismos...");
            
            try (Connection conn = dataSource.getConnection()) {
                DatabaseMetaData metaData = conn.getMetaData();
                
                // Verificar si la tabla existe
                ResultSet tables = metaData.getTables(null, null, "sismos", null);
                if (!tables.next()) {
                    logger.error("¡La tabla sismos no existe en la base de datos!");
                    return;
                }
                
                // Obtener información detallada de la tabla
                List<Map<String, Object>> tableInfo = jdbcTemplate.queryForList(
                        "SHOW TABLE STATUS LIKE 'sismos'");
                if (!tableInfo.isEmpty()) {
                    Map<String, Object> info = tableInfo.get(0);
                    logger.info("Información de la tabla: Engine={}, Rows={}, Auto_increment={}", 
                            info.get("Engine"), info.get("Rows"), info.get("Auto_increment"));
                }
                
                // Obtener estructura completa de la tabla
                List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                        "SHOW COLUMNS FROM sismos");
                logger.info("Estructura de la tabla sismos:");
                for (Map<String, Object> column : columns) {
                    logger.info("  {} {} {} {}", 
                            column.get("Field"),
                            column.get("Type"),
                            column.get("Null").equals("YES") ? "NULL" : "NOT NULL",
                            column.get("Key").equals("PRI") ? "PRIMARY KEY" : "");
                }
            }
        } catch (Exception e) {
            logger.error("ERROR al verificar estructura de la tabla: {}", e.getMessage(), e);
        }
        
        // 2. Verificar conteo inicial
        long count = 0;
        try {
            count = sismoRepository.count();
            logger.info("Número de registros en la tabla sismos: {}", count);
        } catch (Exception e) {
            logger.error("ERROR al contar registros: {}", e.getMessage(), e);
        }
        
        // 3. Probar inserción manual directa con JDBC
        try {
            logger.info("Intentando inserción directa con JDBC...");
            int result = jdbcTemplate.update(
                "INSERT INTO sismos (fecha, hora, magnitud, latitud, longitud, profundidad, " +
                "referencia_localizacion, fecha_utc, hora_utc, estatus) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                LocalDate.now(), "12:00:00", 6.0, 19.5, -99.2, 10.5, 
                "Prueba JDBC", LocalDate.now(), "18:00:00", "TEST"
            );
            logger.info("Inserción JDBC completada. Filas afectadas: {}", result);
        } catch (Exception e) {
            logger.error("ERROR en inserción JDBC: {}", e.getMessage(), e);
        }
        
        // 4. Probar inserción con JPA/Hibernate
        logger.info("Intentando insertar un registro de prueba con JPA...");
        
        try {
            Sismo testSismo = crearSismoPrueba();
            
            // Mostrar el sismo antes de guardar
            logger.info("Sismo a insertar: {}", sismoToString(testSismo));
            
            // Guardar el sismo y obtener el resultado
            Sismo savedSismo = sismoRepository.save(testSismo);
            
            // Verificar si se guardó correctamente
            if (savedSismo != null && savedSismo.getId() != null) {
                logger.info("Sismo guardado exitosamente con ID: {}", savedSismo.getId());
                logger.info("Sismo guardado: {}", savedSismo);
            } else {
                logger.error("El ID del sismo guardado es NULL!");
            }
            
            // Verificar nuevamente el conteo
            count = sismoRepository.count();
            logger.info("Número de registros después de inserción: {}", count);
            
        } catch (Exception e) {
            logger.error("ERROR durante la prueba de inserción JPA: {}", e.getMessage(), e);
        }
        
        // 5. Recuperar y mostrar todos los sismos
        try {
            logger.info("Recuperando todos los sismos...");
            List<Sismo> sismos = sismoRepository.findAll();
            logger.info("Sismos recuperados: {}", sismos.size());
            for (Sismo sismo : sismos) {
                logger.info("  Sismo[id={}]: {}", sismo.getId(), sismo);
            }
        } catch (Exception e) {
            logger.error("ERROR al recuperar sismos: {}", e.getMessage(), e);
        }
        
        logger.info("===== DEPURACIÓN FINALIZADA =====");
    }
    
    private Sismo crearSismoPrueba() {
        Sismo sismo = new Sismo();
        sismo.setFecha(LocalDate.now());
        sismo.setHora("12:30:45");
        sismo.setMagnitud(5.7);
        sismo.setLatitud(19.4326);
        sismo.setLongitud(-99.1332);
        sismo.setProfundidad(12.5);
        sismo.setReferenciaLocalizacion("CDMX, México");
        sismo.setFechaUTC(LocalDate.now());
        sismo.setHoraUTC("18:30:45");
        sismo.setEstatus("VERIFICADO");
        return sismo;
    }
    
    private List<Sismo> crearLotePrueba(int cantidad) {
        List<Sismo> lote = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Sismo sismo = new Sismo();
            sismo.setFecha(LocalDate.now());
            sismo.setHora("13:" + (10 + i) + ":00");
            sismo.setMagnitud(4.0 + (i * 0.5));
            sismo.setLatitud(19.4326 + (i * 0.01));
            sismo.setLongitud(-99.1332 - (i * 0.01));
            sismo.setProfundidad(10.0 + i);
            sismo.setReferenciaLocalizacion("Ubicación de prueba " + (i+1));
            sismo.setFechaUTC(LocalDate.now());
            sismo.setHoraUTC("19:" + (10 + i) + ":00");
            sismo.setEstatus("PRUEBA");
            lote.add(sismo);
        }
        return lote;
    }
    
    private String sismoToString(Sismo sismo) {
        return String.format("Sismo[id=%s, fecha=%s, magnitud=%.1f, latitud=%.4f, longitud=%.4f, referencia=%s]",
                sismo.getId(), sismo.getFecha(), sismo.getMagnitud(),
                sismo.getLatitud(), sismo.getLongitud(), sismo.getReferenciaLocalizacion());
    }
}