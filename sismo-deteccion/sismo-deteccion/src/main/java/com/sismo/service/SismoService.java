package com.sismo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sismo.model.Sismo;
import com.sismo.repository.SismoRepository;

import jakarta.transaction.Transactional;

@Service
public class SismoService {

    private final SismoRepository sismoRepository;

    public SismoService(SismoRepository sismoRepository) {
        this.sismoRepository = sismoRepository;
    }

    // Método para obtener todos los sismos
    public List<Sismo> obtenerTodos() {
        return sismoRepository.findAll();
    }

    // Método para filtrar sismos por magnitud
    public List<Sismo> obtenerPorMagnitud(double magnitud) {
        return sismoRepository.findByMagnitudGreaterThan(magnitud);
    }

    @Transactional
    public void cargarSismosDesdeCSV(MultipartFile file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        List<Sismo> sismosBatch = new ArrayList<>();

        for (CSVRecord record : records) {
            Sismo sismo = new Sismo();
            sismo.setFecha(LocalDate.parse(record.get("Fecha"), dateFormatter));
            sismo.setHora(record.get("Hora"));
            sismo.setMagnitud(parseMagnitud(record.get("Magnitud")));
            sismo.setLatitud(Double.parseDouble(record.get("Latitud")));
            sismo.setLongitud(Double.parseDouble(record.get("Longitud")));
            sismo.setProfundidad(Double.parseDouble(record.get("Profundidad")));
            sismo.setReferenciaLocalizacion(record.get("Referencia de localizacion"));
            sismo.setFechaUTC(LocalDate.parse(record.get("Fecha UTC"), dateFormatter));
            sismo.setHoraUTC(record.get("Hora UTC"));
            sismo.setEstatus(record.get("Estatus"));
            
            sismosBatch.add(sismo);

            // Cada 1000 registros, hacer un "flush" de la lista a la base de datos
            if (sismosBatch.size() >= 1000) {
                sismoRepository.saveAll(sismosBatch);
                sismosBatch.clear();  // Limpiar el batch para el siguiente grupo de registros
            }
        }

        // Asegurarse de guardar los registros restantes si hay menos de 1000 al final
        if (!sismosBatch.isEmpty()) {
            sismoRepository.saveAll(sismosBatch);
            System.out.println("Guardados " + sismosBatch.size() + " registros en la base de datos");
        }
    }

    private Double parseMagnitud(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("no calculable")) {
            return null; // Guarda NULL en la base de datos
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
