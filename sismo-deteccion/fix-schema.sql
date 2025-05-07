-- Script para corregir posibles problemas en la estructura de la tabla sismos
-- Guarda este archivo como fix-schema.sql y ejecútalo dentro del contenedor MySQL

-- 1. Verificar la estructura actual de la tabla
SELECT column_name, column_type, is_nullable, column_key, extra 
FROM information_schema.columns 
WHERE table_schema = 'sismos_db' AND table_name = 'sismos';

-- 2. Corregir la tabla sismos si es necesario
ALTER TABLE sismos MODIFY id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY;

-- 3. Asegurar que las columnas de fechas tengan valores por defecto
ALTER TABLE sismos MODIFY fecha date NOT NULL;
ALTER TABLE sismos MODIFY fecha_utc date NOT NULL;

-- 4. Eliminar las columnas duplicadas si existen (fechautc, horautc)
ALTER TABLE sismos DROP COLUMN IF EXISTS fechautc;
ALTER TABLE sismos DROP COLUMN IF EXISTS horautc;

-- 5. Asegurar que los índices están correctamente configurados
ALTER TABLE sismos ADD INDEX IF NOT EXISTS idx_sismos_magnitud (magnitud);
ALTER TABLE sismos ADD INDEX IF NOT EXISTS idx_sismos_fecha (fecha);
ALTER TABLE sismos ADD INDEX IF NOT EXISTS idx_sismos_ubicacion (latitud, longitud);

-- 6. Verificar el motor de almacenamiento
SHOW TABLE STATUS WHERE Name = 'sismos';

-- 7. Verificar si hay datos en la tabla
SELECT COUNT(*) FROM sismos;

-- 8. Verificar configuración de autocommit 
SHOW VARIABLES LIKE 'autocommit';