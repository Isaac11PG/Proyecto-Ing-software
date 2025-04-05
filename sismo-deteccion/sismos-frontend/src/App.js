import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import SismosMap from './components/SismosMap';
import UserManagement from './components/UserManagement';
import UserProfile from './components/UserProfile';
import Login from './components/Login';
import Register from './components/Register';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/sismos" element={<SismosMap />} />
        <Route path="/admin/gestion-usuarios" element={<UserManagement />} />
        <Route path="/perfil" element={<UserProfile />} />
        <Route path="/" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
}

export default App;