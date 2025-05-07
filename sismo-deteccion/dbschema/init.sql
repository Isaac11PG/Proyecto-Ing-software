-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 27-04-2025 a las 03:01:07
-- Versión del servidor: 9.1.0
-- Versión de PHP: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `sismos_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sismos`
--

DROP TABLE IF EXISTS `sismos`;
CREATE TABLE IF NOT EXISTS `sismos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `hora` varchar(255) DEFAULT NULL,
  `magnitud` double DEFAULT NULL,
  `latitud` double NOT NULL,
  `longitud` double NOT NULL,
  `profundidad` double NOT NULL,
  `referencia_localizacion` varchar(255) DEFAULT NULL,
  `fecha_utc` date NOT NULL,
  `hora_utc` varchar(255) DEFAULT NULL,
  `estatus` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sismos_magnitud` (`magnitud`),
  KEY `idx_sismos_fecha` (`fecha`),
  KEY `idx_sismos_ubicacion` (`latitud`,`longitud`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `email` varchar(64) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_roles`
--

DROP TABLE IF EXISTS `usuario_roles`;
CREATE TABLE IF NOT EXISTS `usuario_roles` (
  `usuario_id` bigint NOT NULL,
  `rol_id` bigint NOT NULL,
  PRIMARY KEY (`usuario_id`,`rol_id`),
  KEY `FKbt9i9yrb9ug88xnh82n9m60pr` (`rol_id`),
  CONSTRAINT `FK_usuario_roles_usuarios` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `FK_usuario_roles_roles` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Insertar roles por defecto si no existen
INSERT IGNORE INTO roles (nombre) VALUES ('ROLE_USER');
INSERT IGNORE INTO roles (nombre) VALUES ('ROLE_ADMIN');
-- Script para insertar datos iniciales de sismos
-- Ejecutar este script después de crear la estructura de la base de datos

-- Desactivar verificación de claves foráneas para acelerar la inserción
SET FOREIGN_KEY_CHECKS = 0;

-- Limpiar la tabla sismos si ya contiene datos (opcional)
-- TRUNCATE TABLE sismos;

-- Insertar datos de sismos
INSERT INTO sismos (fecha, hora, magnitud, latitud, longitud, profundidad, referencia_localizacion, fecha_utc, hora_utc, estatus) VALUES
('2025-01-15', '08:12:34', 5.8, 19.4326, -99.1332, 15.2, 'Ciudad de México, CDMX', '2025-01-15', '14:12:34', 'VERIFICADO'),
('2025-01-17', '14:23:08', 4.6, 16.8302, -99.8981, 10.5, 'Costa de Guerrero', '2025-01-17', '20:23:08', 'VERIFICADO'),
('2025-01-23', '22:41:19', 6.2, 17.5618, -101.2745, 18.7, 'Costa de Michoacán', '2025-01-23', '04:41:19', 'VERIFICADO'),
('2025-01-30', '03:15:42', 4.1, 18.6429, -97.4512, 8.3, 'Puebla', '2025-01-30', '09:15:42', 'VERIFICADO'),
('2025-02-05', '11:07:56', 5.4, 16.2548, -98.1236, 12.9, 'Oaxaca', '2025-02-05', '17:07:56', 'VERIFICADO'),
('2025-02-12', '19:32:11', 4.8, 15.8967, -96.4235, 9.7, 'Costas de Oaxaca', '2025-02-12', '01:32:11', 'VERIFICADO'),
('2025-02-18', '07:49:23', 3.9, 20.1245, -98.7631, 5.4, 'Hidalgo', '2025-02-18', '13:49:23', 'VERIFICADO'),
('2025-02-25', '16:27:39', 5.1, 18.9723, -103.8526, 14.8, 'Colima', '2025-02-25', '22:27:39', 'VERIFICADO'),
('2025-03-04', '01:11:05', 6.8, 17.3214, -101.8954, 25.6, 'Costa de Guerrero', '2025-03-04', '07:11:05', 'VERIFICADO'),
('2025-03-10', '13:58:42', 5.2, 16.7891, -95.2315, 11.3, 'Istmo de Tehuantepec', '2025-03-10', '19:58:42', 'VERIFICADO'),
('2025-03-17', '22:34:18', 4.4, 19.8753, -96.4512, 7.8, 'Veracruz', '2025-03-17', '04:34:18', 'VERIFICADO'),
('2025-03-22', '05:41:32', 5.5, 18.5423, -102.3985, 16.9, 'Michoacán', '2025-03-22', '11:41:32', 'VERIFICADO'),
('2025-03-29', '17:19:54', 4.7, 17.1289, -100.5487, 9.4, 'Guerrero', '2025-03-29', '23:19:54', 'VERIFICADO'),
('2025-04-03', '09:07:21', 4.0, 20.5478, -100.2513, 6.2, 'Querétaro', '2025-04-03', '15:07:21', 'VERIFICADO'),
('2025-04-10', '18:45:33', 6.5, 16.4235, -98.7456, 22.8, 'Costa de Oaxaca', '2025-04-10', '00:45:33', 'VERIFICADO'),
('2025-04-15', '04:23:17', 4.9, 19.2546, -99.5842, 10.1, 'Estado de México', '2025-04-15', '10:23:17', 'VERIFICADO'),
('2025-04-22', '14:56:09', 5.7, 16.9874, -99.5631, 15.6, 'Costas de Guerrero', '2025-04-22', '20:56:09', 'VERIFICADO'),
('2025-04-28', '23:08:42', 4.3, 19.7321, -97.8945, 8.9, 'Puebla', '2025-04-28', '05:08:42', 'VERIFICADO'),
('2025-05-03', '10:37:28', 5.3, 17.2589, -95.7863, 13.7, 'Oaxaca', '2025-05-03', '16:37:28', 'VERIFICADO'),
('2025-05-07', '02:15:51', 4.5, 18.6542, -103.1258, 9.2, 'Colima', '2025-05-07', '08:15:51', 'VERIFICADO');

-- Sismos más recientes (último día)
INSERT INTO sismos (fecha, hora, magnitud, latitud, longitud, profundidad, referencia_localizacion, fecha_utc, hora_utc, estatus) VALUES
('2025-05-06', '09:48:22', 6.1, 16.4578, -94.8932, 21.3, 'Chiapas', '2025-05-06', '15:48:22', 'VERIFICADO'),
('2025-05-06', '14:25:37', 5.2, 19.2145, -96.7832, 14.2, 'Veracruz', '2025-05-06', '20:25:37', 'VERIFICADO'),
('2025-05-06', '18:12:05', 4.6, 18.7523, -98.2417, 8.7, 'Morelos', '2025-05-06', '00:12:05', 'VERIFICADO'),
('2025-05-06', '21:34:19', 5.8, 17.9658, -101.3594, 17.5, 'Michoacán', '2025-05-06', '03:34:19', 'VERIFICADO'),
('2025-05-07', '00:41:57', 4.9, 20.2387, -99.5684, 10.8, 'Hidalgo', '2025-05-07', '06:41:57', 'VERIFICADO');

-- Sismos de gran magnitud históricos
INSERT INTO sismos (fecha, hora, magnitud, latitud, longitud, profundidad, referencia_localizacion, fecha_utc, hora_utc, estatus) VALUES
('2017-09-19', '13:14:40', 7.1, 18.4073, -98.7173, 51.0, 'Puebla, México', '2017-09-19', '18:14:40', 'HISTÓRICO'),
('2020-06-23', '10:29:02', 7.5, 16.2308, -95.7125, 26.3, 'Oaxaca, México', '2020-06-23', '15:29:02', 'HISTÓRICO'),
('2022-09-19', '13:05:09', 7.7, 18.3662, -103.1778, 15.1, 'Michoacán, México', '2022-09-19', '18:05:09', 'HISTÓRICO'),
('1985-09-19', '07:17:47', 8.1, 18.1900, -102.5600, 27.0, 'Michoacán, México', '1985-09-19', '13:17:47', 'HISTÓRICO'),
('2023-05-10', '16:18:23', 5.8, 17.2436, -100.5698, 18.9, 'Guerrero, México', '2023-05-10', '21:18:23', 'HISTÓRICO');

-- Script para insertar datos adicionales de sismos desde el catálogo SSN
-- Se seleccionan sismos con magnitud >= 4.0 y algunos representativos menores

INSERT INTO sismos (fecha, hora, magnitud, latitud, longitud, profundidad, referencia_localizacion, fecha_utc, hora_utc, estatus) VALUES
-- Sismos significativos del catálogo 2025-05-01 al 2025-05-06
('2025-05-01', '11:00:44', 5.2, 15.03, -94.158, 16.1, '125 km al SUROESTE de TONALA, CHIS', '2025-05-01', '17:00:44', 'VERIFICADO'),
('2025-05-02', '04:16:57', 4.1, 16.464, -101.072, 10.0, '96 km al SUROESTE de TECPAN, GRO', '2025-05-02', '10:16:57', 'VERIFICADO'),
('2025-05-02', '05:00:15', 4.2, 15.028, -94.165, 16.1, '125 km al SUROESTE de TONALA, CHIS', '2025-05-02', '11:00:15', 'VERIFICADO'),
('2025-05-02', '17:08:07', 4.2, 14.35, -93.25, 10.0, '121 km al SUROESTE de HUIXTLA, CHIS', '2025-05-02', '23:08:07', 'VERIFICADO'),
('2025-05-03', '09:55:25', 4.0, 27.416, -111.353, 10.0, '71 km al SUROESTE de H GUAYMAS, SON', '2025-05-03', '15:55:25', 'VERIFICADO'),
('2025-05-04', '03:59:56', 4.0, 17.082, -100.382, 27.1, '15 km al SURESTE de ATOYAC DE ALVAREZ, GRO', '2025-05-04', '09:59:56', 'VERIFICADO'),
('2025-05-04', '13:36:09', 4.0, 21.02, -106.112, 13.2, '100 km al NOROESTE de IXTAPA, JAL', '2025-05-04', '19:36:09', 'VERIFICADO'),
('2025-05-04', '13:37:45', 4.2, 15.919, -95.999, 61.8, '22 km al NORESTE de CRUCECITA, OAX', '2025-05-04', '19:37:45', 'VERIFICADO'),
('2025-05-05', '02:28:12', 5.8, 16.351, -92.953, 269.7, '28 km al SUROESTE de ACALA, CHIS', '2025-05-05', '08:28:12', 'VERIFICADO'),
('2025-05-05', '07:20:22', 4.1, 14.159, -92.292, 15.9, '60 km al SUR de CD HIDALGO, CHIS', '2025-05-05', '13:20:22', 'VERIFICADO'),
('2025-05-05', '15:03:56', 4.2, 16.293, -98.007, 5.6, '7 km al SURESTE de PINOTEPA NACIONAL, OAX', '2025-05-05', '21:03:56', 'VERIFICADO'),

-- Algunos sismos representativos entre 3.5 y 4.0
('2025-05-01', '05:45:49', 3.9, 13.957, -92.098, 47.8, '80 km al SUR de CD HIDALGO, CHIS', '2025-05-01', '11:45:49', 'VERIFICADO'),
('2025-05-01', '18:03:51', 3.8, 16.017, -95.159, 3.6, '19 km al SUR de SALINA CRUZ, OAX', '2025-05-02', '00:03:51', 'VERIFICADO'),
('2025-05-02', '04:44:00', 3.8, 14.941, -94.195, 19.1, '134 km al SUROESTE de PIJIJIAPAN, CHIS', '2025-05-02', '10:44:00', 'VERIFICADO'),
('2025-05-02', '16:50:25', 4.0, 17.465, -94.948, 135.8, '46 km al SUR de SAYULA DE ALEMAN, VER', '2025-05-02', '22:50:25', 'VERIFICADO'),
('2025-05-03', '09:11:00', 3.8, 15.631, -94.303, 58.2, '77 km al SUROESTE de TONALA, CHIS', '2025-05-03', '15:11:00', 'VERIFICADO'),
('2025-05-04', '03:39:33', 3.9, 15.542, -94.635, 27.7, '93 km al SURESTE de SALINA CRUZ, OAX', '2025-05-04', '09:39:33', 'VERIFICADO'),
('2025-05-04', '16:24:20', 3.9, 17.173, -101.343, 5.0, '41 km al SUR de PETATLAN, GRO', '2025-05-04', '22:24:20', 'VERIFICADO'),
('2025-05-05', '08:11:17', 3.8, 15.202, -94.647, 16.1, '123 km al SURESTE de SALINA CRUZ, OAX', '2025-05-05', '14:11:17', 'VERIFICADO'),
('2025-05-05', '11:01:53', 3.9, 18.866, -107.139, 8.3, '274 km al OESTE de CIHUATLAN, JAL', '2025-05-05', '17:01:53', 'VERIFICADO'),
('2025-05-05', '11:27:33', 3.8, 15.936, -92.716, 209.8, '47 km al SUROESTE de VENUSTIANO CARRANZA, CHIS', '2025-05-05', '17:27:33', 'VERIFICADO'),

-- Sismos en CDMX y área metropolitana
('2025-05-04', '05:50:29', 1.9, 19.399, -99.18, 1.0, 'MIGUEL HIDALGO, CDMX', '2025-05-04', '11:50:29', 'VERIFICADO'),

-- Enjambre sísmico en Guerrero (ejemplo representativo)
('2025-05-03', '05:43:54', 3.3, 16.749, -99.201, 22.7, '21 km al ESTE de SAN MARCOS, GRO', '2025-05-03', '11:43:54', 'VERIFICADO'),

-- Sismos en zonas poco comunes
('2025-05-01', '01:09:18', 3.4, 31.412, -115.595, 8.2, '84 km al NOROESTE de SAN FELIPE, BC', '2025-05-01', '07:09:18', 'VERIFICADO'),
('2025-05-02', '01:13:26', 3.5, 25.089, -109.511, 10.0, '94 km al SUROESTE de LOS MOCHIS, SIN', '2025-05-02', '07:13:26', 'VERIFICADO'),
('2025-05-05', '11:15:33', 2.0, 23.186, -109.719, 10.0, '14 km al NORTE de SAN JOSE DEL CABO, BCS', '2025-05-05', '17:15:33', 'VERIFICADO');
-- Restablecer verificación de claves foráneas
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO usuarios (nombre, email, password) 
VALUES (
    'admin', 
    'admin@sismo.com', 
    '$2a$10$VR42hnzIavoQHC3fGkP3SugzaKGxaI7JpMCE8w6rVKhTq9FlNR8B6'
)
ON DUPLICATE KEY UPDATE email = VALUES(email), password = VALUES(password);

INSERT INTO usuario_roles (usuario_id, rol_id)
VALUES (
    (SELECT id FROM usuarios WHERE nombre = 'admin' LIMIT 1),
    (SELECT id FROM roles WHERE nombre = 'ROLE_ADMIN' LIMIT 1)
)
ON DUPLICATE KEY UPDATE usuario_id = VALUES(usuario_id), rol_id = VALUES(rol_id);

-- Insertar roles por defecto si no existen
INSERT IGNORE INTO roles (nombre) VALUES ('ROLE_USER');
INSERT IGNORE INTO roles (nombre) VALUES ('ROLE_ADMIN');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;