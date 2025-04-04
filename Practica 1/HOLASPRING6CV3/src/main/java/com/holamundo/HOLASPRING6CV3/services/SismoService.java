package com.holamundo.HOLASPRING6CV3.services;

import com.holamundo.HOLASPRING6CV3.models.SismoModel;
import com.holamundo.HOLASPRING6CV3.repositories.SismoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SismoService {

    private final SismoRepository sismoRepository;

    @Autowired
    public SismoService(SismoRepository sismoRepository) {
        this.sismoRepository = sismoRepository;
    }

    public List<SismoModel> procesarArchivoCSV(MultipartFile archivo) throws IOException {
        List<SismoModel> sismos = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(archivo.getInputStream()));
        
        String linea;
        boolean datosIniciados = false;
        DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        while ((linea = br.readLine()) != null) {
            // Ignorar líneas de encabezado y comentarios
            if (linea.startsWith("Fecha,Hora")) {
                datosIniciados = true;
                continue;
            }
            
            if (!datosIniciados || linea.trim().isEmpty() || linea.startsWith("\"")) {
                continue;
            }
            
            try {
                // Procesar la línea CSV con cuidado con las comillas en los campos
                String[] campos = this.parsearCSV(linea);
                
                if (campos.length < 10) {
                    continue;  // Saltamos líneas mal formateadas
                }

                SismoModel sismo = new SismoModel();
                sismo.setFecha(LocalDate.parse(campos[0], fechaFormatter));
                sismo.setHora(LocalTime.parse(campos[1], horaFormatter));
                sismo.setMagnitud(Double.parseDouble(campos[2]));
                sismo.setLatitud(Double.parseDouble(campos[3]));
                sismo.setLongitud(Double.parseDouble(campos[4]));
                sismo.setProfundidad(Double.parseDouble(campos[5]));
                sismo.setReferencia(campos[6]);
                sismo.setFechaUTC(LocalDate.parse(campos[7], fechaFormatter));
                sismo.setHoraUTC(LocalTime.parse(campos[8], horaFormatter));
                sismo.setEstatus(campos[9]);

                sismos.add(sismo);
            } catch (Exception e) {
                // Log para depuración
                System.err.println("Error procesando línea: " + linea);
                e.printStackTrace();
            }
        }
        
        // Guardar todos los sismos en la base de datos
        return sismoRepository.saveAll(sismos);
    }
    
    private String[] parsearCSV(String linea) {
        List<String> tokens = new ArrayList<>();
        boolean entreComillas = false;
        StringBuilder sb = new StringBuilder();
        
        for (char c : linea.toCharArray()) {
            if (c == '"') {
                entreComillas = !entreComillas;
            } else if (c == ',' && !entreComillas) {
                tokens.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        
        // Añadir el último token
        tokens.add(sb.toString());
        
        return tokens.toArray(new String[0]);
    }
    
    public List<SismoModel> obtenerTodos() {
        return sismoRepository.findAll();
    }
}