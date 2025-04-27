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
      // Cambiar a JSON en lugar de form-data para cumplir con la API REST
      const userData = {
        username: username,
        password: password
      };
      
      const response = await axios.post(loginUrl, userData, {
        headers: {
          'Content-Type': 'application/json', // Cambiado para enviar como JSON
          'Accept': 'application/json',
        },
        withCredentials: true // Crucial para mantener la sesión entre peticiones
      });
      
      console.log('Respuesta recibida:', response);
      
      if (response.data && !response.data.error) {
        setMensaje(response.data.message || 'Login exitoso');
        setError(false);
        
        // Guardar info de usuario
        localStorage.setItem('username', response.data.username);
        
        // Si la respuesta ya incluye el rol, guardarlo directamente
        if (response.data.roles && response.data.roles.length > 0) {
          localStorage.setItem('userRole', response.data.roles[0]);
          
          // Redirigir basado en rol
          setTimeout(() => {
            const role = response.data.roles[0];
            if (role === 'ROLE_ADMIN') {
              navigate('/admin/dashboard');
            } else {
              navigate('/user/dashboard/tabla');
            }
          }, 1000);
        } else {
          // Verificar el estado de autenticación y obtener roles después del login
          try {
            console.log('Verificando estado de autenticación en:', `${API_URL}/api/auth/check`);
            
            const authCheckResponse = await axios.get(`${API_URL}/api/auth/check`, {
              withCredentials: true,
              headers: {
                'Accept': 'application/json'
              }
            });

            console.log('Respuesta de verificación recibida:', authCheckResponse.data);
            
            if (authCheckResponse.data.authenticated) {
              // Guardar roles desde la respuesta de verificación
              if (authCheckResponse.data.roles && authCheckResponse.data.roles.length > 0) {
                localStorage.setItem('userRole', authCheckResponse.data.roles[0]);
                
                // Redirigir basado en rol
                const role = authCheckResponse.data.roles[0];
                if (role === 'ROLE_ADMIN') {
                  setTimeout(() => navigate('/admin/dashboard'), 1000);
                } else {
                  setTimeout(() => navigate('/user/dashboard/tabla'), 1000);
                }
              } else {
                // Fallback si no hay roles en la respuesta
                localStorage.setItem('userRole', 'ROLE_USER');
                setTimeout(() => navigate('/user/dashboard/tabla'), 1000);
              }
            } else {
              // Caso raro: login exitoso pero la verificación dice que no está autenticado
              console.warn('Login exitoso pero la verificación indica que no está autenticado');
              // Intenta la ruta de backup para obtener el rol
              fallbackGetRole();
            }
          } catch (error) {
            console.error('Error al verificar autenticación:', error);
            // Usar el método de backup para obtener el rol
            fallbackGetRole();
          }
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
        
        if (error.response.status === 404) {
          setMensaje('Error: La URL de login no existe. Verifique la configuración del servidor.');
        } else if (error.response.status === 401 || error.response.status === 403) {
          setMensaje('Usuario o contraseña incorrectos');
        } else {
          setMensaje(`Error del servidor: ${error.response.status} - ${error.response.data?.message || 'Error desconocido'}`);
        }
      } else if (error.request) {
        console.error('No se recibió respuesta del servidor:', error.request);
        setMensaje('No se pudo conectar con el servidor. Verifique su conexión y que el servidor esté activo.');
      } else {
        console.error('Error al configurar la petición:', error.message);
        setMensaje(`Error: ${error.message}`);
      }
      
      setError(true);
    } finally {
      setLoading(false);
    }
  };

  // Función de respaldo para obtener el rol del usuario
  const fallbackGetRole = async () => {
    try {
      console.log('Intentando obtener rol desde:', `${API_URL}/api/home`);
      
      const roleResponse = await axios.get(`${API_URL}/api/home`, {
        withCredentials: true,
        headers: {
          'Accept': 'application/json'
        }
      });

      console.log('Respuesta de rol recibida:', roleResponse.data);
      
      // Determinar el rol basado en la respuesta
      if (roleResponse.data.isAdmin) {
        localStorage.setItem('userRole', 'ROLE_ADMIN');
        setTimeout(() => navigate('/admin/dashboard'), 1000);
      } else {
        localStorage.setItem('userRole', 'ROLE_USER');
        setTimeout(() => navigate('/user/dashboard/tabla'), 1000);
      }
    } catch (error) {
      console.error('Error al obtener el rol (respaldo):', error);
      localStorage.setItem('userRole', 'ROLE_USER'); // Rol por defecto
      setTimeout(() => navigate('/user/dashboard/tabla'), 1000);
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