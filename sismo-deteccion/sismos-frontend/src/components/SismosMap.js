import React, { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import axios from "axios";
import API_URL from "../config";

const SismosMap = () => {
    const [sismos, setSismos] = useState([]);

    useEffect(() => {
        axios.get(API_URL)
            .then(response => setSismos(response.data))
            .catch(error => console.error("Error al obtener sismos:", error));
    }, []);

    return (
        <MapContainer center={[23.6345, -102.5528]} zoom={5} style={{ height: "500px", width: "100%" }}>
            <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
            {sismos.map((sismo, index) => (
                <Marker key={index} position={[sismo.latitud, sismo.longitud]}>
                    <Popup>
                        <strong>Fecha:</strong> {sismo.fecha} <br />
                        <strong>Hora:</strong> {sismo.hora} <br />
                        <strong>Magnitud:</strong> {sismo.magnitud || "No calculable"} <br />
                        <strong>Profundidad:</strong> {sismo.profundidad} km <br />
                        <strong>Ubicación:</strong> {sismo.referenciaLocalizacion}
                    </Popup>
                </Marker>
            ))}
        </MapContainer>
    );
};

export default SismosMap;