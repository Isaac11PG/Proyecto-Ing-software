package com.holamundo.HOLASPRING6CV3.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Controller
public class MySQLController {

    // Parámetros de conexión
    private static final String URL = "jdbc:mysql://localhost:3306/tarea2?useSSL=false&serverTimezone=UTC";
    private static final String USER = "admin"; // Usuario de la base de datos
    private static final String PASSWORD = "admin"; // Contraseña de la base de datos

    @GetMapping("/testConnection")
    public String testConnection(Model model) {
        Connection connection = null;
        String message = "";

        try {
            // Intentar conectar a la base de datos
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            message = "Conexión exitosa a la base de datos MySQL";
        } catch (SQLException e) {
            message = "Error al conectar a la base de datos: " + e.getMessage();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                message = "Error al cerrar la conexión: " + e.getMessage();
            }
        }

        // Pasar el mensaje al modelo
        model.addAttribute("message", message);

        // Retornar el nombre de la vista (HTML) que mostrará el mensaje
        return "connectionResult"; // Debes crear un archivo connectionResult.html
    }
}

