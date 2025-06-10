import React from 'react';
import { View, Text, Button, StyleSheet } from 'react-native';

// Temporal: para simular la navegación a Home
const LoginScreen = ({ navigation }) => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Login Screen</Text>
      <Button
        title="Ir a Home (Simulado)"
        onPress={() => navigation.replace('MainApp')} // Usamos replace para que no pueda volver al login
      />
      {/* Aquí irán tus campos de email, contraseña, etc. */}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  title: {
    fontSize: 24,
    marginBottom: 20,
  },
});

export default LoginScreen;