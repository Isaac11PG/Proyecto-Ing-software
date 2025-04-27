package com.holamundo.HOLASPRING6CV3.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sismos")
public class SismoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate fecha;
    private LocalTime hora;
    private Double magnitud;
    private Double latitud;
    private Double longitud;
    private Double profundidad;
    private String referencia;
    private LocalDate fechaUTC;
    private LocalTime horaUTC;
    private String estatus;

    // Constructores
    public SismoModel() {
    }

    public SismoModel(LocalDate fecha, LocalTime hora, Double magnitud, Double latitud, Double longitud, 
                      Double profundidad, String referencia, LocalDate fechaUTC, LocalTime horaUTC, String estatus) {
        this.fecha = fecha;
        this.hora = hora;
        this.magnitud = magnitud;
        this.latitud = latitud;
        this.longitud = longitud;
        this.profundidad = profundidad;
        this.referencia = referencia;
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

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
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

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public LocalDate getFechaUTC() {
        return fechaUTC;
    }

    public void setFechaUTC(LocalDate fechaUTC) {
        this.fechaUTC = fechaUTC;
    }

    public LocalTime getHoraUTC() {
        return horaUTC;
    }

    public void setHoraUTC(LocalTime horaUTC) {
        this.horaUTC = horaUTC;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    @Override
    public String toString() {
        return "SismoModel{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", hora=" + hora +
                ", magnitud=" + magnitud +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", profundidad=" + profundidad +
                ", referencia='" + referencia + '\'' +
                ", fechaUTC=" + fechaUTC +
                ", horaUTC=" + horaUTC +
                ", estatus='" + estatus + '\'' +
                '}';
    }
}