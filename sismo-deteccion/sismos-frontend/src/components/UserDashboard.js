import React from 'react';
import { Link, Routes, Route, useNavigate } from 'react-router-dom';
import SismosTable from './SismosTable';
import SismosMap from './SismosMap';
import GrafoVisualizacion from './GrafoVisualizacion';

const UserDashboard = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    sessionStorage.clear();

    navigate('/login'); // Redirige al login
  };

  return (
    <div>
      <h1>Panel de Usuario</h1>
      <button 
        onClick={handleLogout}
        style={{
          position: 'absolute',
          top: 20,
          right: 20,
          padding: '8px 12px',
          background: '#e53935',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer'
        }}
      >
        Cerrar sesión
      </button>
      <nav>
        <ul>
          <li>
            <Link to="/user/dashboard/tabla" replace>
              Tabla de Sismos
            </Link>
          </li>
          <li>
            <Link to="/user/dashboard/mapa" replace>
              Mapa de Sismos
            </Link>
          </li>
          <li>
            <Link to="/user/dashboard/grafo" replace>
              Grafo de Conocimiento
            </Link>
          </li>
        </ul>
      </nav>
      <Routes>
        <Route path="tabla" element={<SismosTable />} />
        <Route path="mapa" element={<SismosMap />} />
        <Route path="grafo" element={<GrafoVisualizacion />} />
      </Routes>
    </div>
  );
};

export default UserDashboard;