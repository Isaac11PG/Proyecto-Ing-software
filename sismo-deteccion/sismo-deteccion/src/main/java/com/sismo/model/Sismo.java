package com.sismo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase Sismo mejorada con anotaciones JPA apropiadas
 * Asegura la correcta generación de IDs y mapeo de columnas
 */
@Entity
@Table(name = "sismos", indexes = {
    @Index(name = "idx_sismos_magnitud", columnList = "magnitud"),
    @Index(name = "idx_sismos_fecha", columnList = "fecha"),
    @Index(name = "idx_sismos_ubicacion", columnList = "latitud, longitud")
})
public class Sismo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
    
    @Column(name = "hora")
    private String hora;
    
    @Column(name = "magnitud")
    private Double magnitud;
    
    @Column(name = "latitud", nullable = false)
    private Double latitud;
    
    @Column(name = "longitud", nullable = false)
    private Double longitud;
    
    @Column(name = "profundidad", nullable = false)
    private Double profundidad;
    
    @Column(name = "referencia_localizacion")
    private String referenciaLocalizacion;
    
    @Column(name = "fecha_utc", nullable = false)
    private LocalDate fechaUTC;
    
    @Column(name = "hora_utc")
    private String horaUTC;
    
    @Column(name = "estatus")
    private String estatus;
    
    // Constructor vacío necesario para JPA
    public Sismo() {
    }
    
    // Constructor completo para crear instancias más fácilmente
    public Sismo(LocalDate fecha, String hora, Double magnitud, Double latitud,
                 Double longitud, Double profundidad, String referenciaLocalizacion,
                 LocalDate fechaUTC, String horaUTC, String estatus) {
        this.fecha = fecha;
        this.hora = hora;
        this.magnitud = magnitud;
        this.latitud = latitud;
        this.longitud = longitud;
        this.profundidad = profundidad;
        this.referenciaLocalizacion = referenciaLocalizacion;
        this.fechaUTC = fechaUTC;
        this.horaUTC = horaUTC;
        this.estatus = estatus;
    }
    
    // Getters y Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Double getMagnitud() {
        return magnitud;
    }

    public void setMagnitud(Double magnitud) {
        this.magnitud = magnitud;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getProfundidad() {
        return profundidad;
    }

    public void setProfundidad(Double profundidad) {
        this.profundidad = profundidad;
    }

    public String getReferenciaLocalizacion() {
        return referenciaLocalizacion;
    }

    public void setReferenciaLocalizacion(String referenciaLocalizacion) {
        this.referenciaLocalizacion = referenciaLocalizacion;
    }

    public LocalDate getFechaUTC() {
        return fechaUTC;
    }

    public void setFechaUTC(LocalDate fechaUTC) {
        this.fechaUTC = fechaUTC;
    }

    public String getHoraUTC() {
        return horaUTC;
    }

    public void setHoraUTC(String horaUTC) {
        this.horaUTC = horaUTC;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sismo sismo = (Sismo) o;
        return Objects.equals(id, sismo.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Sismo{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", hora='" + hora + '\'' +
                ", magnitud=" + magnitud +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", profundidad=" + profundidad +
                ", referenciaLocalizacion='" + referenciaLocalizacion + '\'' +
                ", fechaUTC=" + fechaUTC +
                ", horaUTC='" + horaUTC + '\'' +
                ", estatus='" + estatus + '\'' +
                '}';
    }
}