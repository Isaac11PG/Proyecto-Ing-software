import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import API_URL from '../config';

const Register = () => {
  const [nombre, setNombre] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [mensaje, setMensaje] = useState('');
  const [error, setError] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {    
    e.preventDefault();
    if (password !== confirmPassword) {
      setMensaje('Las contraseñas no coinciden');
      setError(true);
      return;
    }
    try {
      // Enviar como JSON, porque ahora el backend espera un @RequestBody
      const response = await axios.post(`${API_URL}/api/registro`, {
        nombre, // Se mantiene como "nombre" aquí
        email,
        password,
        confirmPassword
      });
      
      // Manejar la respuesta del backend
      if (response.data && !response.data.error) {
        setMensaje(response.data.mensaje || 'Registro exitoso. Ahora puedes iniciar sesión.');
        setError(false);
        // Redirigir al login después de un breve retraso
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else {
        setMensaje(response.data.mensaje || 'Error en el registro');
        setError(true);
      }
    } catch (error) {
      setMensaje('Error al registrar el usuario. Es posible que el nombre de usuario o el email ya exista.');
      setError(true);
      console.error('Error en el registro', error);
    }
  };

  return (
    <div>
      <h2>Registro</h2>
      {mensaje && (
        <div style={{ color: error ? 'red' : 'green' }}>
          {mensaje}
        </div>
      )}
      <form onSubmit={handleSubmit}>
        <div>
          <label>Nombre:</label>
          <input type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} required />
        </div>
        <div>
          <label>Email:</label>
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </div>
        <div>
          <label>Password:</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </div>
        <div>
          <label>Confirm Password:</label>
          <input type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required />
        </div>
        <button type="submit">Register</button>
      </form>
    </div>
  );
};

export default Register;