import React from 'react';
import { Link, Routes, Route } from 'react-router-dom';
import SismosTable from './SismosTable';
import SismosMap from './SismosMap';
import GrafoVisualizacion from './GrafoVisualizacion';
import UploadCSV from './UploadCSV';
import UserManagement from './UserManagement';

const AdminDashboard = () => {
  return (
    <div>
      <h1>Panel de Administración</h1>
      <nav>
        <ul>
          <li>
            <Link to="/admin/dashboard/tabla">Tabla de Sismos</Link>
          </li>
          <li>
            <Link to="/admin/dashboard/mapa">Mapa de Sismos</Link>
          </li>
          <li>
            <Link to="/admin/dashboard/grafo">Grafo de Conocimiento</Link>
          </li>
          <li>
            <Link to="/admin/dashboard/cargar-csv">Cargar CSV</Link>
          </li>
          <li>
            <Link to="/admin/dashboard/usuarios">Gestión de Usuarios</Link>
          </li>
        </ul>
      </nav>

      <Routes>
        <Route path="/admin/dashboard/tabla" element={<SismosTable />} />
        <Route path="/admin/dashboard/mapa" element={<SismosMap />} />
        <Route path="/admin/dashboard/grafo" element={<GrafoVisualizacion />} />
        <Route path="/admin/dashboard/cargar-csv" element={<UploadCSV />} />
        <Route path="/admin/dashboard/usuarios" element={<UserManagement />} />
      </Routes>
    </div>
  );
};

export default AdminDashboard;