import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import API_URL from '../config';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [mensaje, setMensaje] = useState('');
  const [error, setError] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMensaje('');
    
    const loginUrl = `${API_URL}/api/auth/login`;
    console.log('Intentando login en:', loginUrl);
    
    try {
      const response = await axios.post(loginUrl, {
        username,
        password
      }, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      console.log('Respuesta recibida:', response);
      
      if (response.data && !response.data.error) {
        setMensaje(response.data.message || 'Login exitoso');
        setError(false);
        
        // Guardar info de usuario
        localStorage.setItem('username', response.data.username);

        try {
          const roleResponse = await axios.get(`${API_URL}/api/home`, {
            withCredentials: true // Importante para enviar las cookies de sesión
          });
          
          // Determinar el rol basado en la respuesta
          if (roleResponse.data.isAdmin) {
            localStorage.setItem('userRole', 'ROLE_ADMIN');
          } else if (roleResponse.data.isUser) {
            localStorage.setItem('userRole', 'ROLE_USER');
          }
          
          // Redirigir a home
          setTimeout(() => {
            navigate('/home');
          }, 1000);
        } catch (error) {
          console.error('Error al obtener el rol:', error);
          // Si falla, redirigir a home de todas formas
          navigate('/home');
        }
        
      } else {
        setMensaje(response.data.message || 'Credenciales inválidas');
        setError(true);
      }
    } catch (error) {
      console.error('Error en la petición de login:', error);
      
      if (error.response) {
        console.error('Detalles del error del servidor:', {
          status: error.response.status,
          data: error.response.data
        });
        
        // Mostrar mensaje específico según el código de error
        if (error.response.status === 404) {
          setMensaje('Error: La URL de login no existe. Verifique la configuración del servidor.');
        } else if (error.response.status === 401) {
          setMensaje('Usuario o contraseña incorrectos');
        } else {
          setMensaje(`Error del servidor: ${error.response.status}`);
        }
      } else if (error.request) {
        console.error('No se recibió respuesta del servidor:', error.request);
        setMensaje('No se pudo conectar con el servidor. Verifique su conexión.');
      } else {
        console.error('Error al configurar la petición:', error.message);
        setMensaje(`Error: ${error.message}`);
      }
      
      setError(true);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <h2>Iniciar Sesión</h2>
      
      {mensaje && (
        <div className={`message ${error ? 'error' : 'success'}`}>
          {mensaje}
        </div>
      )}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="username">Usuario:</label>
          <input 
            type="text" 
            id="username"
            value={username} 
            onChange={(e) => setUsername(e.target.value)} 
            required 
            disabled={loading}
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="password">Contraseña:</label>
          <input 
            type="password" 
            id="password"
            value={password} 
            onChange={(e) => setPassword(e.target.value)} 
            required 
            disabled={loading}
          />
        </div>
        
        <button type="submit" disabled={loading}>
          {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
        </button>
      </form>
      
      <div className="register-link">
        ¿No tienes una cuenta? <a href="/register">Regístrate aquí</a>
      </div>
    </div>
  );
};

export default Login;