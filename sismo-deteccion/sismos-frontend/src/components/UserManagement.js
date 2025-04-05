import React, { useEffect, useState } from 'react';
import axios from 'axios';

const UserManagement = () => {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await axios.get('/api/admin/usuarios');
        setUsers(response.data);
      } catch (error) {
        console.error('Error obteniendo usuarios', error);
      }
    };
    fetchUsers();
  }, []);

  const handleDelete = async (id) => {
    try {
      await axios.delete(`/api/admin/usuarios/${id}`);
      setUsers(users.filter(user => user.id !== id));
    } catch (error) {
      console.error('Error eliminando usuario', error);
    }
  };

  return (
    <div>
      <h2>Gestión de Usuarios</h2>
      <table>
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Email</th>
            <th>Rol</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {users.map(user => (
            <tr key={user.id}>
              <td>{user.nombre}</td>
              <td>{user.email}</td>
              <td>{user.rol}</td>
              <td>
                <button onClick={() => handleDelete(user.id)}>Eliminar</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default UserManagement;