// Función para establecer el tema
function setTheme(theme) {
    const html = document.documentElement;
    
    // Eliminar clases existentes
    html.classList.remove('theme-light', 'theme-dark');
    
    // Almacenar la preferencia en localStorage
    localStorage.setItem('theme', theme);
    
    // Aplicar el tema seleccionado
    if (theme === 'light') {
        html.classList.add('theme-light');
    } else if (theme === 'dark') {
        html.classList.add('theme-dark');
    } else if (theme === 'system') {
        // Usar la preferencia del sistema
        if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
            html.classList.add('theme-dark');
        } else {
            html.classList.add('theme-light');
        }
        
        // Escuchar cambios en la preferencia del sistema
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
            if (localStorage.getItem('theme') === 'system') {
                if (e.matches) {
                    html.classList.remove('theme-light');
                    html.classList.add('theme-dark');
                } else {
                    html.classList.remove('theme-dark');
                    html.classList.add('theme-light');
                }
            }
        });
    }
    
    // Actualizar los iconos seleccionados en el selector
    updateThemeSelector(theme);
}

// Función para actualizar la apariencia del selector de tema
function updateThemeSelector(theme) {
    document.querySelectorAll('.theme-option').forEach(option => {
        if (option.dataset.theme === theme) {
            option.classList.add('active');
        } else {
            option.classList.remove('active');
        }
    });
}

// Inicializar tema al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    // Obtener tema guardado o usar el del usuario logueado
    const savedTheme = localStorage.getItem('theme') || document.body.dataset.userTheme || 'system';
    setTheme(savedTheme);
    
    // Configurar los botones del selector de tema
    document.querySelectorAll('.theme-option').forEach(option => {
        option.addEventListener('click', () => {
            const theme = option.dataset.theme;
            setTheme(theme);
            
            // Enviar solicitud al servidor para guardar la preferencia si el usuario está logueado
            if (document.body.dataset.userLoggedIn === 'true') {
                fetch('/cambiar-tema', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                    },
                    body: `theme=${theme}&referer=${window.location.pathname}`
                });
            }
        });
    });
});