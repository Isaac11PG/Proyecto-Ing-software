import React, { useEffect, useState, useRef } from 'react';
import { Network } from 'vis-network';
import { DataSet } from 'vis-data';
import axios from '../AxiosConfig';
import API_URL from '../config';

const GrafoVisualizacion = () => {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const networkContainer = useRef(null);
    const networkInstance = useRef(null);

    useEffect(() => {
        const fetchGraphData = async () => {
            try {
                setLoading(true);
                const response = await axios.get(`${API_URL}/api/grafos/visualizacion`, {
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    }
                    }
                );
                
                const { nodes, edges } = response.data;

                console.log('nodes:', nodes);
                console.log('edges:', edges);
                
                // Configurar opciones de visualización
                const options = {
                    nodes: {
                        shape: 'dot',
                        size: 16,
                        font: {
                            size: 12,
                            color: '#000000'
                        },
                        borderWidth: 2
                    },
                    edges: {
                        width: 1,
                        font: {
                            size: 10,
                            align: 'middle'
                        },
                        color: { inherit: 'from' },
                        arrows: {
                            to: { enabled: true, scaleFactor: 0.5 }
                        }
                    },
                    groups: {
                        sismo: {
                            color: { background: '#97C2FC', border: '#2B7CE9' },
                            shape: 'dot'
                        },
                        ubicacion: {
                            color: { background: '#FB7E81', border: '#FA0010' },
                            shape: 'diamond'
                        }
                    },
                    physics: {
                        enabled: true,
                        hierarchicalRepulsion: {
                            centralGravity: 0.5,
                            springLength: 150,
                            springConstant: 0.01,
                            nodeDistance: 120,
                            damping: 0.09
                        },
                        solver: 'hierarchicalRepulsion'
                    }
                };
                
                // Crear datasets para vis.js
                const nodesDataset = new DataSet(nodes);
                const edgesDataset = new DataSet(edges);
                
                // Crear y renderizar la red
                try {
                    if (networkContainer.current) {
                        if (networkInstance.current) {
                            networkInstance.current.destroy();
                        }
                        networkInstance.current = new Network(
                            networkContainer.current,
                            { nodes: nodesDataset, edges: edgesDataset },
                            options
                        );
                    }
                    setLoading(false);
                } catch (visError) {
                    console.error("Error en vis-network:", visError);
                    setError('Error al visualizar el grafo.');
                    setLoading(false);
                }

                if (!Array.isArray(nodes) || !Array.isArray(edges)) {
                    setError('Los datos recibidos no tienen el formato correcto.');
                    setLoading(false);
                    return;
                }
                
                setLoading(false);
            } catch (error) {
                if (error.response && error.response.status === 401) {
                    setError('Por favor inicia sesión para ver esta visualización.');
                    // window.location.href = '/login';
                } else {
                    setError('Error al cargar los datos del grafo. Por favor, intenta de nuevo más tarde.');
                }
                
                setLoading(false);
            }
        };
        
        fetchGraphData();
        
        // Limpiar al desmontar
        return () => {
            if (networkInstance.current) {
                networkInstance.current.destroy();
            }
        };
    }, []);

    const cargarDatosExistentes = async () => {
        try {
            setLoading(true);
            await axios.post(`${API_URL}/api/grafos/cargar-existentes`, {}, {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            });
            // Recargar la visualización
            window.location.reload();
        } catch (error) {
            console.error('Error al cargar datos existentes a Neo4j:', error);
            setError('Error al cargar datos existentes a Neo4j.');
            setLoading(false);
        }
    };

    return (
        <div>
            <h2>Visualización de Grafos de Sismos</h2>
            
            <button 
                onClick={cargarDatosExistentes}
                disabled={loading}
                style={{
                    padding: '10px 15px',
                    margin: '10px 0',
                    backgroundColor: '#4CAF50',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer'
                }}
            >
                {loading ? 'Cargando...' : 'Cargar Datos Existentes a Neo4j'}
            </button>
            
            {error && <div style={{ color: 'red', margin: '10px 0' }}>{error}</div>}
            
            {loading && <div>Cargando visualización del grafo...</div>}
            
            <div 
                ref={networkContainer} 
                style={{ 
                    width: '100%', 
                    height: '600px',
                    border: '1px solid #ddd',
                    borderRadius: '4px',
                    backgroundColor: '#f8f8f8' 
                }}
            ></div>
            
            <div style={{ marginTop: '20px' }}>
                <h3>Leyenda</h3>
                <ul>
                    <li><span style={{ color: '#2B7CE9' }}>●</span> Sismos</li>
                    <li><span style={{ color: '#FA0010' }}>◆</span> Ubicaciones</li>
                    <li><span style={{ color: '#2B7CE9' }}>→</span> CERCANO_A: Sismos cercanos geográficamente</li>
                    <li><span style={{ color: '#2B7CE9' }}>→</span> SIMILAR_MAGNITUD: Sismos con magnitud similar</li>
                    <li><span style={{ color: '#2B7CE9' }}>→</span> OCURRIDO_EN: Relación entre sismo y ubicación</li>
                </ul>
            </div>
        </div>
    );
};

export default GrafoVisualizacion;