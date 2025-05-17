#!/bin/bash

# Script para probar la inserción manual de datos en la tabla sismos
# Ejecutar esto desde fuera del contenedor

echo "Probando inserción manual de datos en la tabla sismos..."

# Insertar un registro de prueba
docker exec sismo-mysql mysql -usismouser -psismopassword -D sismos_db -e "
INSERT INTO sismos (fecha, hora, magnitud, latitud, longitud, profundidad, 
                    referencia_localizacion, fecha_utc, hora_utc, estatus)
VALUES (CURDATE(), '12:30:45', 5.7, 19.4326, -99.1332, 12.5, 
       'CDMX, México - INSERCIÓN MANUAL', CURDATE(), '18:30:45', 'MANUAL');
"

# Verificar si se insertó correctamente
echo -e "\nVerificando si se insertó el registro:"
docker exec sismo-mysql mysql -usismouser -psismopassword -D sismos_db -e "
SELECT * FROM sismos WHERE referencia_localizacion LIKE '%MANUAL%';
"

# Contar registros totales
echo -e "\nTotal de registros en la tabla:"
docker exec sismo-mysql mysql -usismouser -psismopassword -D sismos_db -e "
SELECT COUNT(*) as 'Total registros' FROM sismos;
"