import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import SismosTable from './components/SismosTable';
import GrafoVisualizacion from './components/GrafoVisualizacion';
import axios from 'axios';
import './App.css';

function App() {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [uploadStatus, setUploadStatus] = useState(null);
  const [uploadError, setUploadError] = useState(null);

  // Función para manejar la selección de archivos
  const handleFileSelect = (event) => {
    setFile(event.target.files[0]);
    setUploadStatus(null);
    setUploadError(null);
  };

  // Función para subir el archivo CSV
  const handleFileUpload = async () => {
    if (!file) {
      setUploadError("Por favor selecciona un archivo CSV");
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    try {
      setLoading(true);
      setUploadError(null);
      
      // Actualiza la URL para apuntar a tu endpoint de carga
      const response = await axios.post('http://localhost:8080/api/sismos/cargar', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        timeout: 600000 // 10 minutos de timeout para archivos grandes
      });

      setUploadStatus(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error al cargar el archivo:", error);
      setUploadError(`Error al cargar el archivo: ${error.message}`);
      setLoading(false);
    }
  };

  return (
    <Router>
      <div className="App">
        <header className="App-header">
          <h1>Sistema de Detección de Sismos</h1>
          <nav>
            <ul style={{ display: 'flex', listStyle: 'none', gap: '20px', padding: 0 }}>
              <li><Link to="/">Tabla de Sismos</Link></li>
              <li><Link to="/grafos">Visualización de Grafos</Link></li>
            </ul>
          </nav>
        </header>
        
        <main style={{ padding: '20px' }}>
          {/* Sección para cargar CSV */}
          <div style={{
            marginBottom: '30px',
            padding: '20px',
            border: '1px solid #ddd',
            borderRadius: '8px',
            backgroundColor: '#f9f9f9'
          }}>
            <h2>Cargar Datos de Sismos (CSV)</h2>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
              <input 
                type="file" 
                accept=".csv" 
                onChange={handleFileSelect}
                style={{
                  padding: '10px',
                  border: '1px solid #ccc',
                  borderRadius: '4px',
                  width: '100%',
                  maxWidth: '500px'
                }} 
              />
              
              <button 
                onClick={handleFileUpload}
                disabled={!file || loading}
                style={{
                  padding: '10px 15px',
                  backgroundColor: !file || loading ? '#cccccc' : '#4CAF50',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: !file || loading ? 'not-allowed' : 'pointer',
                  width: '200px'
                }}
              >
                {loading ? 'Cargando...' : 'Subir Archivo CSV'}
              </button>
              
              {loading && (
                <div style={{ marginTop: '10px' }}>
                  <p>Cargando archivo grande. Esto puede tomar varios minutos...</p>
                  <div style={{ 
                    width: '100%', 
                    height: '10px', 
                    backgroundColor: '#e0e0e0',
                    borderRadius: '5px',
                    overflow: 'hidden'
                  }}>
                    <div style={{
                      width: '100%',
                      height: '100%',
                      backgroundColor: '#4CAF50',
                      animation: 'progress 2s linear infinite',
                      backgroundImage: 'linear-gradient(45deg, rgba(255, 255, 255, 0.15) 25%, transparent 25%, transparent 50%, rgba(255, 255, 255, 0.15) 50%, rgba(255, 255, 255, 0.15) 75%, transparent 75%, transparent)',
                      backgroundSize: '1rem 1rem',
                    }}></div>
                  </div>
                </div>
              )}
              
              {uploadStatus && (
                <div style={{ 
                  marginTop: '10px', 
                  padding: '10px', 
                  backgroundColor: '#dff0d8', 
                  color: '#3c763d',
                  borderRadius: '4px' 
                }}>
                  {uploadStatus}
                </div>
              )}
              
              {uploadError && (
                <div style={{ 
                  marginTop: '10px', 
                  padding: '10px', 
                  backgroundColor: '#f2dede', 
                  color: '#a94442',
                  borderRadius: '4px' 
                }}>
                  {uploadError}
                </div>
              )}
              
              <div style={{ marginTop: '10px' }}>
                <p><strong>Notas:</strong></p>
                <ul>
                  <li>El archivo debe estar en formato CSV</li>
                  <li>El archivo puede tardar varios minutos en procesarse si es grande</li>
                  <li>El sistema procesará automáticamente los datos tanto para MySQL como para la base de datos de grafos</li>
                </ul>
              </div>
            </div>
          </div>
          
          {/* Rutas para la navegación */}
          <Routes>
            <Route path="/" element={<SismosTable />} />
            <Route path="/grafos" element={<GrafoVisualizacion />} />
          </Routes>
        </main>
        
        {/* Estilos para la animación de la barra de progreso */}
        <style>
          {`
            @keyframes progress {
              0% {
                background-position: 0 0;
              }
              100% {
                background-position: 1rem 0;
              }
            }
          `}
        </style>
      </div>
    </Router>
  );
}

export default App;