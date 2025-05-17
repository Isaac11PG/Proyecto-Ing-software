#!/bin/bash
set -e

# Esperar a que Neo4j esté disponible
echo "Esperando a que Neo4j esté disponible..."
until curl -s http://neo4j:7474 > /dev/null; do
  echo "Neo4j aún no está disponible - esperando..."
  sleep 5
done
echo "Neo4j está disponible!"

# Crear índices y restricciones para mejorar el rendimiento
echo "Creando índices y restricciones en Neo4j..."
cypher-shell -u neo4j -p $NEO4J_PASSWORD -a bolt://neo4j:7687 "CREATE CONSTRAINT IF NOT EXISTS FOR (s:Sismo) REQUIRE s.codigo IS UNIQUE;"
cypher-shell -u neo4j -p $NEO4J_PASSWORD -a bolt://neo4j:7687 "CREATE CONSTRAINT IF NOT EXISTS FOR (u:Ubicacion) REQUIRE u.codigo IS UNIQUE;"
cypher-shell -u neo4j -p $NEO4J_PASSWORD -a bolt://neo4j:7687 "CREATE INDEX IF NOT EXISTS FOR (s:Sismo) ON (s.magnitud);"
cypher-shell -u neo4j -p $NEO4J_PASSWORD -a bolt://neo4j:7687 "CREATE INDEX IF NOT EXISTS FOR (s:Sismo) ON (s.latitud, s.longitud);"

echo "Inicialización de Neo4j completada."