package com.sismo.service;

import com.sismo.model.Sismo;
import com.sismo.model.graph.SismoNode;
import com.sismo.model.graph.UbicacionNode;
import com.sismo.repository.graph.SismoNodeRepository;
import com.sismo.repository.graph.UbicacionNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GrafoService {
    
    private final SismoNodeRepository sismoNodeRepository;
    private final UbicacionNodeRepository ubicacionNodeRepository;
    
    public GrafoService(SismoNodeRepository sismoNodeRepository, 
                       UbicacionNodeRepository ubicacionNodeRepository) {
        this.sismoNodeRepository = sismoNodeRepository;
        this.ubicacionNodeRepository = ubicacionNodeRepository;
    }
    
    @Transactional
    public SismoNode crearNodoSismo(Sismo sismo) {
        // Crear nodo para el sismo
        SismoNode sismoNode = new SismoNode();
        sismoNode.setFecha(sismo.getFecha());
        sismoNode.setHora(sismo.getHora());
        sismoNode.setMagnitud(sismo.getMagnitud());
        sismoNode.setLatitud(sismo.getLatitud());
        sismoNode.setLongitud(sismo.getLongitud());
        sismoNode.setProfundidad(sismo.getProfundidad());
        sismoNode.setReferenciaLocalizacion(sismo.getReferenciaLocalizacion());
        sismoNode.setFechaUTC(sismo.getFechaUTC());
        sismoNode.setHoraUTC(sismo.getHoraUTC());
        sismoNode.setEstatus(sismo.getEstatus());
        
        // Buscar o crear nodo de ubicación
        Optional<UbicacionNode> ubicacionExistente = ubicacionNodeRepository
            .buscarUbicacionCercana(sismo.getLatitud(), sismo.getLongitud());
        
        UbicacionNode ubicacion;
        if (ubicacionExistente.isPresent()) {
            ubicacion = ubicacionExistente.get();
        } else {
            ubicacion = new UbicacionNode();
            ubicacion.setNombre(sismo.getReferenciaLocalizacion());
            ubicacion.setLatitud(sismo.getLatitud());
            ubicacion.setLongitud(sismo.getLongitud());
            ubicacion = ubicacionNodeRepository.save(ubicacion);
        }
        
        // Establecer relación entre sismo y ubicación
        sismoNode.setUbicacion(ubicacion);
        sismoNode = sismoNodeRepository.save(sismoNode);
        
        // Buscar sismos cercanos y establecer relaciones
        establecerRelacionesSismosCercanos(sismoNode);
        
        // Buscar sismos con magnitud similar y establecer relaciones
        establecerRelacionesMagnitudSimilar(sismoNode);
        
        return sismoNode;
    }
    
    @Transactional
    public void cargarSismosAGrafo(List<Sismo> sismos) {
        for (Sismo sismo : sismos) {
            crearNodoSismo(sismo);
        }
    }
    
    @Transactional
    public void establecerRelacionesSismosCercanos(SismoNode sismoNode) {
        // Buscar sismos en un radio de 50km
        List<SismoNode> sismosCercanos = sismoNodeRepository
            .buscarSismosCercanos(sismoNode.getLatitud(), sismoNode.getLongitud(), 25);
        
        // Filtrar para no incluir el mismo sismo
        sismosCercanos = sismosCercanos.stream()
            .filter(s -> !s.getId().equals(sismoNode.getId()))
            .collect(Collectors.toList());
        
        // Establecer relaciones (limitando a máximo 5 sismos cercanos)
        int count = 0;
        for (SismoNode cercano : sismosCercanos) {
            if (count >= 5) break;
            sismoNode.getSismosCercanos().add(cercano);
            count++;
        }
        
        sismoNodeRepository.save(sismoNode);
    }
    
    @Transactional
    public void establecerRelacionesMagnitudSimilar(SismoNode sismoNode) {
        if (sismoNode.getMagnitud() == null) return;
        
        // Rango de tolerancia para magnitud similar (+/- 0.5)
        double magnitudMin = sismoNode.getMagnitud() - 0.5;
        double magnitudMax = sismoNode.getMagnitud() + 0.5;
        
        // Buscar sismos con magnitud similar usando una consulta personalizada
        // Esta es una simplificación, considera usar una consulta Cypher más específica
        List<SismoNode> sismosSimilares = sismoNodeRepository.findAll().stream()
            .filter(s -> s.getMagnitud() != null && 
                   s.getMagnitud() >= magnitudMin && 
                   s.getMagnitud() <= magnitudMax &&
                   !s.getId().equals(sismoNode.getId()))
            .limit(5)
            .collect(Collectors.toList());
        
        // Establecer relaciones
        for (SismoNode similar : sismosSimilares) {
            sismoNode.getSismosSimilares().add(similar);
        }
        
        sismoNodeRepository.save(sismoNode);
    }
    
    public List<SismoNode> obtenerTodosLosNodosSismo() {
        return sismoNodeRepository.findAll();
    }
    
    public List<UbicacionNode> obtenerTodasLasUbicaciones() {
        return ubicacionNodeRepository.findAll();
    }
}