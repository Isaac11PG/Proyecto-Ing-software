import React, { useState } from "react";
import axios from "axios";
import API_URL from "../config";

const UploadCSV = () => {
    const [file, setFile] = useState(null);
    const [message, setMessage] = useState("");

    const handleFileChange = (event) => {
        setFile(event.target.files[0]);
    };

    const handleUpload = async () => {
        if (!file) {
            setMessage("Selecciona un archivo CSV.");
            return;
        }

        const formData = new FormData();
        formData.append("file", file);

        try {
            const response = await axios.post(`${API_URL}api/sismos/cargar`, formData, {
                headers: { "Content-Type": "multipart/form-data" },
            });
            setMessage("Archivo subido con éxito.");
            console.log(response);  // Muestra la respuesta en la consola
        } catch (error) {
            setMessage("Error al cargar el archivo.");
            console.error("Error:", error.response || error);  // Muestra el error en la consola
        }
    };

    return (
        <div>
            <h2>Subir CSV de Sismos</h2>
            <input type="file" accept=".csv" onChange={handleFileChange} />
            <button onClick={handleUpload}>Subir</button>
            {message && <p>{message}</p>}
        </div>
    );
};

export default UploadCSV;