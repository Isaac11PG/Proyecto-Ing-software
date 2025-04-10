import React from 'react';
import { Link, Routes, Route } from 'react-router-dom';
import SismosTable from './SismosTable';
import SismosMap from './SismosMap';
import GrafoVisualizacion from './GrafoVisualizacion';

const UserDashboard = () => {
  return (
    <div>
      <h1>Panel de Usuario</h1>
      <nav>
        <ul>
          <li>
            <Link to="/user/dashboard/tabla">Tabla de Sismos</Link>
          </li>
          <li>
            <Link to="/user/dashboard/mapa">Mapa de Sismos</Link>
          </li>
          <li>
            <Link to="/user/dashboard/grafo">Grafo de Conocimiento</Link>
          </li>
        </ul>
      </nav>

      <Routes>
        <Route path="/user/dashboard/tabla" element={<SismosTable />} />
        <Route path="/user/dashboard/mapa" element={<SismosMap />} />
        <Route path="/user/dashboard/grafo" element={<GrafoVisualizacion />} />
      </Routes>
    </div>
  );
};

export default UserDashboard;