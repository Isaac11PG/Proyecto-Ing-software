import React, { useEffect, useState } from "react";
import axios from "axios";
import API_URL from "../config";
import "./SismoTable.css";

const SismosTable = () => {
    const [sismos, setSismos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Estilos en línea
    const styles = {
        table: {
            width: '100%',
            borderCollapse: 'collapse',
            marginTop: '20px'
        },
        cell: {
            border: '1px solid #ddd',
            padding: '8px',
            textAlign: 'left'
        },
        headerCell: {
            border: '1px solid #ddd',
            padding: '8px',
            textAlign: 'left',
            backgroundColor: '#f2f2f2',
            fontWeight: 'bold'
        },
        evenRow: {
            backgroundColor: '#f9f9f9'
        },
        error: {
            color: 'red',
            padding: '10px',
            border: '1px solid red',
            backgroundColor: '#ffe6e6',
            margin: '10px 0'
        },
        container: {
            padding: '20px',
            maxWidth: '1200px',
            margin: '0 auto'
        }
    };

    useEffect(() => {
        // Agrega console.log para depurar
        console.log("Intentando obtener datos de:", API_URL);
        
        setLoading(true);
        axios.get(API_URL)
            .then(response => {
                console.log("Datos recibidos:", response.data);
                setSismos(response.data);
                setLoading(false);
            })
            .catch(error => {
                console.error("Error al obtener sismos:", error);
                setError(`Error: ${error.message}`);
                setLoading(false);
            });
    }, []);

    if (loading) return <div style={styles.container}>Cargando datos de sismos...</div>;
    if (error) return <div style={{...styles.container, ...styles.error}}>{error}</div>;
    if (sismos.length === 0) return <div style={styles.container}>No hay registros de sismos para mostrar.</div>;

    return (
        <div style={styles.container}>
            <h2>Lista de Sismos</h2>
            <p>Total de registros: {sismos.length}</p>
            <table style={styles.table}>
                <thead>
                    <tr>
                        <th style={styles.headerCell}>Fecha</th>
                        <th style={styles.headerCell}>Hora</th>
                        <th style={styles.headerCell}>Magnitud</th>
                        <th style={styles.headerCell}>Latitud</th>
                        <th style={styles.headerCell}>Longitud</th>
                        <th style={styles.headerCell}>Profundidad</th>
                        <th style={styles.headerCell}>Ubicación</th>
                    </tr>
                </thead>
                <tbody>
                    {sismos.map((sismo, index) => (
                        <tr key={index} style={index % 2 === 0 ? {} : styles.evenRow}>
                            <td style={styles.cell}>{sismo.fecha}</td>
                            <td style={styles.cell}>{sismo.hora}</td>
                            <td style={styles.cell}>{sismo.magnitud || "No calculable"}</td>
                            <td style={styles.cell}>{sismo.latitud}</td>
                            <td style={styles.cell}>{sismo.longitud}</td>
                            <td style={styles.cell}>{sismo.profundidad} km</td>
                            <td style={styles.cell}>{sismo.referenciaLocalizacion}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default SismosTable;