FROM neo4j:4.4

# Instalar herramientas necesarias
RUN apt-get update && \
    apt-get install -y curl wget && \
    rm -rf /var/lib/apt/lists/*

# Copiar script de inicialización
COPY init-neo4j.sh /init-neo4j.sh
RUN chmod +x /init-neo4j.sh

# Comando a ejecutar cuando el contenedor inicie
ENTRYPOINT ["/init-neo4j.sh"]