# Sistema de Alerta y Visualización de Sismos 

## Descripción del Proyecto
Este es un proyecto de aplicación web de gestión de usuarios desarrollado con Spring Boot, que incluye funcionalidades de autenticación, registro, perfil de usuario y administración.

## Características Principales
- Registro de usuarios
- Autenticación con Spring Security
- Gestión de roles (USER, ADMIN)
- Perfil de usuario
- Panel de administración para gestión de usuarios

## Funcionalidades Nuevas

### 1. **Tema Claro/Oscuro**
- La aplicación permite seleccionar entre tema claro, oscuro o predeterminado del sistema.
- La configuración de tema se guarda para cada usuario, permitiendo personalizar su experiencia.

### 2. **Carga de Datos CSV**
- Permite cargar información sísmica a la base de datos desde archivos CSV del SSN.
- Se incluye una interfaz administrativa para gestionar la importación de datos, facilitando la carga masiva de información sísmica.

### 3. **Visualización de Sismos**
- Mapa interactivo que muestra la ubicación de los sismos.
- Filtros de búsqueda para encontrar sismos por diferentes criterios como fecha, magnitud y ubicación.

## Requisitos Previos
- Java 21
- Maven
- MySQL 8.0+

## Configuración del Entorno

### 1. Base de Datos
- Crear base de datos MySQL:
  ```sql
  CREATE DATABASE tarea2;
  ```

- Credenciales de base de datos (configuradas en `application.properties`):
  - Usuario: `admin`
  - Contraseña: `admin`
  - Base de datos: `tarea2`

### 2. Configuración de Base de Datos
El proyecto incluye un `schema.sql` que se ejecutará automáticamente al iniciar la aplicación. Este script:
- Crea la base de datos
- Configura tablas de usuarios y roles
- Inserta roles predeterminados (ROLE_ADMIN, ROLE_USER)
- Crea un usuario de base de datos

### 3. Dependencias
Las dependencias principales incluyen:
- Spring Boot Web
- Spring Security
- Spring Data JPA
- Thymeleaf
- MySQL Connector
- Bootstrap (WebJars)

## Instalación y Ejecución

### Clonar el Repositorio
```bash
git clone <URL_DEL_REPOSITORIO>
cd HOLASPRING6CV3
```

### Compilar el Proyecto
```bash
mvn clean install
```

### Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

## Acceso a la Aplicación
- URL Base: `http://localhost:8080`
- Páginas:
  - Login: `/login`
  - Registro: `/registro`
  - Home: `/home`
  - Perfil: `/perfil`
  - Gestión de Usuarios (Admin): `/admin/gestion-usuarios`

## Usuarios Predeterminados
El sistema se inicializa con roles de usuario. Puedes registrar nuevos usuarios desde la página de registro.

## Pruebas
Ejecutar pruebas unitarias:
```bash
mvn test
```

## Seguridad
- Contraseñas encriptadas con BCrypt
- Roles de usuario (USER, ADMIN)
- Protección de rutas según roles

## Tecnologías Utilizadas
- Java 21
- Spring Boot 3.4.2
- Spring Security
- Thymeleaf
- MySQL
- Bootstrap 5

## Consideraciones de Seguridad
- CSRF deshabilitado para simplicidad (ajustar en producción)
- Validación de contraseñas
- Manejo de errores personalizado

## Capturas de pantalla

![image](https://github.com/user-attachments/assets/572646bc-ac23-415e-9c7b-93082e79647d)

![image](https://github.com/user-attachments/assets/e41c1457-1006-4e9f-8a38-1857a637eaba)

![image](https://github.com/user-attachments/assets/cdd814ac-579c-4c33-8ec9-2454d0971d7f)

