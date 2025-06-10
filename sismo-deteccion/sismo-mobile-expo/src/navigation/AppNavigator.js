import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';

import LoginScreen from '../screens/Auth/LoginScreen';
// Importa RegisterScreen cuando lo crees
// import RegisterScreen from '../screens/Auth/RegisterScreen';

import HomeScreen from '../screens/Main/HomeScreen';
// Importa otras pantallas principales cuando las crees
// import MapScreen from '../screens/Main/MapScreen';
// import SismosListScreen from '../screens/Main/SismosListScreen';

const AuthStack = createStackNavigator();
const MainStack = createStackNavigator(); // Stack para la app principal

// Stack para las pantallas de autenticación
const AuthStackNavigator = () => {
  return (
    <AuthStack.Navigator screenOptions={{ headerShown: false }} initialRouteName="Login">
      <AuthStack.Screen name="Login" component={LoginScreen} />
      {/* <AuthStack.Screen name="Register" component={RegisterScreen} /> */}
    </AuthStack.Navigator>
  );
};

// Stack para las pantallas principales de la aplicación (después del login)
const MainAppNavigator = () => {
  return (
    <MainStack.Navigator initialRouteName="Home">
      <MainStack.Screen name="Home" component={HomeScreen} options={{ title: 'Inicio Sismos' }}/>
      {/* <MainStack.Screen name="Map" component={MapScreen} /> */}
      {/* <MainStack.Screen name="SismosList" component={SismosListScreen} /> */}
    </MainStack.Navigator>
  );
};

// Navegador principal que decide qué stack mostrar
// Por ahora, simularemos el estado de autenticación.
// Luego, esto dependerá de si el usuario tiene un token válido.
const AppNavigator = () => {
  // ESTADO DE AUTENTICACIÓN SIMULADO - CAMBIAREMOS ESTO MÁS ADELANTE
  const [isAuthenticated, setIsAuthenticated] = React.useState(false); // Inicia como no autenticado

  // Esta es una forma MUY SIMPLIFICADA de manejar el cambio de estado para la prueba.
  // En una app real, esto se manejaría con Context API, Redux, o AsyncStorage.
  // Por ahora, LoginScreen y HomeScreen navegan directamente usando navigation.replace
  // y este AppNavigator decide el stack inicial basado en `isAuthenticated`.
  // Para una prueba más completa del cambio de stack, necesitaríamos un AuthContext.

  // Por ahora, el cambio de stack se maneja con navigation.replace en las pantallas
  // para ir a 'Auth' o 'MainApp'. El AppNavigator simplemente define esos stacks.
  // Para que el AppNavigator realmente cambie entre AuthStackNavigator y MainAppNavigator
  // necesitaríamos una forma de que las pantallas de login/logout actualicen
  // el estado `isAuthenticated` aquí (o en un contexto global).

  // La lógica de navegación que te di antes en LoginScreen y HomeScreen
  // usa navigation.replace('Auth') y navigation.replace('MainApp')
  // Esto funciona porque los stacks están definidos aquí, pero no cambia
  // el `isAuthenticated` de ESTE componente.
  // Para que este componente cambie los stacks, necesitaríamos una lógica de estado global.

  // Vamos a crear los dos stacks y el NavigationContainer los manejará
  // según cómo naveguemos entre ellos.
  const RootStack = createStackNavigator();

  return (
    <NavigationContainer>
      <RootStack.Navigator screenOptions={{ headerShown: false }}>
        <RootStack.Screen name="Auth" component={AuthStackNavigator} />
        <RootStack.Screen name="MainApp" component={MainAppNavigator} />
      </RootStack.Navigator>
    </NavigationContainer>
  );
};

export default AppNavigator;