// Detección inteligente del entorno
const isDocker = process.env.REACT_APP_IS_DOCKER === 'true';

// Configuración basada en el entorno
const API_URL = isDocker ? "http://backend:8080" : "http://localhost:8081";

export default API_URL;