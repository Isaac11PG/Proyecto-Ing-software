package com.sismo.model.graph;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Node("Ubicacion")
public class UbicacionNode {
    @Id @GeneratedValue
    private Long id;
    
    @Property("nombre")
    private String nombre;
    
    @Property("latitud")
    private Double latitud;
    
    @Property("longitud")
    private Double longitud;
    
    // Relación con sismos ocurridos en esta ubicación
    @Relationship(type = "TIENE_SISMO", direction = Relationship.Direction.OUTGOING)
    private Set<SismoNode> sismos = new HashSet<>();
}