package com.sismo.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sismo.model.Sismo;
import com.sismo.repository.SismoRepository;

/**
 * Servicio mejorado para diagnosticar y corregir problemas con la carga de CSV
 */
@Service
public class SismoServiceFix {
    
    private static final Logger logger = LoggerFactory.getLogger(SismoServiceFix.class);
    
    @Autowired
    private SismoRepository sismoRepository;
    
    @Autowired
    private SismoService sismoService;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * Método de diagnóstico para verificar el estado de la BD y realizar inserciones de prueba
     */
    public void diagnosticarBaseDeDatos() {
        logger.info("===== INICIANDO DIAGNÓSTICO DE BASE DE DATOS =====");
        
        // 1. Verificar conexión
        try (Connection conn = dataSource.getConnection()) {
            logger.info("Conexión a la base de datos establecida correctamente");
            logger.info("URL: {}", conn.getMetaData().getURL());
            logger.info("Autocommit: {}", conn.getAutoCommit());
        } catch (Exception e) {
            logger.error("ERROR al conectar a la base de datos: {}", e.getMessage(), e);
            return;
        }
        
        // 2. Verificar permisos
        try {
            jdbcTemplate.execute("SHOW GRANTS");
            logger.info("Consulta de permisos ejecutada correctamente");
        } catch (Exception e) {
            logger.error("ERROR al consultar permisos: {}", e.getMessage(), e);
        }
        
        // 3. Contar registros
        try {
            Long count = sismoRepository.count();
            logger.info("Número actual de registros en la tabla sismos: {}", count);
        } catch (Exception e) {
            logger.error("ERROR al contar registros: {}", e.getMessage(), e);
        }
        
        // 4. Probar inserción directa con JDBC
        try {
            logger.info("Intentando inserción directa con JDBC...");
            int result = jdbcTemplate.update(
                "INSERT INTO sismos (fecha, hora, magnitud, latitud, longitud, profundidad, " +
                "referencia_localizacion, fecha_utc, hora_utc, estatus) VALUES " +
                "(CURRENT_DATE(), '12:00:00', 6.0, 19.5, -99.2, 10.5, " +
                "'Diagnóstico JDBC', CURRENT_DATE(), '18:00:00', 'DIAG')"
            );
            logger.info("Inserción JDBC completada. Filas afectadas: {}", result);
        } catch (Exception e) {
            logger.error("ERROR en inserción JDBC: {}", e.getMessage(), e);
        }
        
        // 5. Verificar transacciones
        probarTransacciones();
        
        logger.info("===== FIN DEL DIAGNÓSTICO =====");
    }
    
    /**
     * Prueba explícitamente diferentes configuraciones de transacciones
     */
    private void probarTransacciones() {
        try {
            insertarConTransaccionExplicita();
            logger.info("Inserción con transacción explícita completada.");
        } catch (Exception e) {
            logger.error("ERROR en transacción explícita: {}", e.getMessage(), e);
        }
        
        try {
            insertarSinTransaccion();
            logger.info("Inserción sin transacción completada.");
        } catch (Exception e) {
            logger.error("ERROR en inserción sin transacción: {}", e.getMessage(), e);
        }
        
        try {
            insertarConJPA();
            logger.info("Inserción con JPA completada.");
        } catch (Exception e) {
            logger.error("ERROR en inserción JPA: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Inserción con transacción explícita JDBC
     */
    private void insertarConTransaccionExplicita() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO sismos (fecha, hora, magnitud, latitud, longitud, profundidad, " +
                    "referencia_localizacion, fecha_utc, hora_utc, estatus) VALUES " +
                    "(CURRENT_DATE(), ?, ?, ?, ?, ?, ?, CURRENT_DATE(), ?, ?)")) {
                
                ps.setString(1, "13:00:00");
                ps.setDouble(2, 5.5);
                ps.setDouble(3, 19.6);
                ps.setDouble(4, -99.3);
                ps.setDouble(5, 12.0);
                ps.setString(6, "Prueba Transacción Explícita");
                ps.setString(7, "19:00:00");
                ps.setString(8, "EXPLICITO");
                
                int result = ps.executeUpdate();
                logger.info("Filas afectadas antes de commit: {}", result);
                
                conn.commit();
                logger.info("Commit realizado correctamente");
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.info("Rollback ejecutado");
                } catch (SQLException ex) {
                    logger.error("Error en rollback", ex);
                }
            }
            throw new RuntimeException("Error en transacción explícita", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error al cerrar conexión", e);
                }
            }
        }
    }
    
    /**
     * Inserción directa sin control de transacción
     */
    private void insertarSinTransaccion() {
        try {
            int result = jdbcTemplate.update(
                "INSERT INTO sismos (fecha, hora, magnitud, latitud, longitud, profundidad, " +
                "referencia_localizacion, fecha_utc, hora_utc, estatus) VALUES " +
                "(CURRENT_DATE(), '14:00:00', 5.8, 19.7, -99.4, 11.0, " +
                "'Sin Transacción', CURRENT_DATE(), '20:00:00', 'DIRECTO')"
            );
            logger.info("Inserción directa completada. Filas afectadas: {}", result);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error en inserción directa", e);
        }
    }
    
    /**
     * Inserción con JPA/Hibernate
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertarConJPA() {
        Sismo sismo = new Sismo();
        sismo.setFecha(java.time.LocalDate.now());
        sismo.setHora("15:00:00");
        sismo.setMagnitud(6.2);
        sismo.setLatitud(19.8);
        sismo.setLongitud(-99.5);
        sismo.setProfundidad(13.0);
        sismo.setReferenciaLocalizacion("Prueba JPA");
        sismo.setFechaUTC(java.time.LocalDate.now());
        sismo.setHoraUTC("21:00:00");
        sismo.setEstatus("JPA_TEST");
        
        Sismo guardado = sismoRepository.save(sismo);
        logger.info("Sismo guardado con JPA, ID: {}", guardado.getId());
    }
    
    /**
     * Versión corregida del método de carga de CSV
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cargarSismosDesdeArchivoConFix(File csvFile) throws IOException {
        logger.info("Iniciando carga de CSV con versión corregida: {}", csvFile.getAbsolutePath());
        
        // Procesar el archivo con el servicio original
        sismoService.cargarSismosDesdeArchivo(csvFile);
        
        // Verificar conteo después de procesar
        Long count = sismoRepository.count();
        logger.info("Después de procesar CSV, la tabla tiene {} registros", count);
    }
    
    /**
     * Verificar los datos ya procesados pero posiblemente no guardados
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void verificarUltimaSubida() {
        long count = sismoRepository.count();
        logger.info("Número actual de registros en la tabla sismos: {}", count);
        
        try {
            // Forzar un flush para asegurar que Hibernate envíe cualquier cambio pendiente a la BD
            List<Sismo> ultimosSismos = sismoRepository.findAll();
            logger.info("Total de sismos recuperados: {}", ultimosSismos.size());
            
            if (!ultimosSismos.isEmpty()) {
                Sismo ultimo = ultimosSismos.get(ultimosSismos.size() - 1);
                logger.info("Último sismo: ID={}, Fecha={}, Magnitud={}, Referencia={}",
                    ultimo.getId(), ultimo.getFecha(), ultimo.getMagnitud(), ultimo.getReferenciaLocalizacion());
            }
        } catch (Exception e) {
            logger.error("Error al verificar última subida: {}", e.getMessage(), e);
        }
    }
}