import React, { useEffect, useState } from 'react';
import { View, Text, ActivityIndicator, StyleSheet, ScrollView, Dimensions, TouchableOpacity } from 'react-native';
import { MapView, Marker, Circle } from 'expo-maps';
import sismoService from '../../services/sismoService';
import { useNavigation } from '@react-navigation/native';

const colorByMagnitud = (magnitud) => {
  if (!magnitud) return "#3388ff";
  if (magnitud >= 7) return "#ff0000";
  if (magnitud >= 6) return "#ff8800";
  if (magnitud >= 5) return "#ffff00";
  return "#3388ff";
};

const SismosMapScreen = () => {
  const [sismos, setSismos] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigation = useNavigation();
  const [selectedSismo, setSelectedSismo] = useState(null);

  useEffect(() => {
    sismoService.getSismosByMagnitud(5)
      .then(setSismos)
      .catch(() => setSismos([]))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <View style={styles.loaderBox}>
        <ActivityIndicator size="large" color="#FF9200" />
        <Text style={{marginTop:12}}>Cargando datos de sismos...</Text>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Visualización de Sismos</Text>
      <View style={styles.legendRow}>
        <View style={styles.legendItem}>
          <View style={[styles.legendCircle, {backgroundColor: "#ff0000"}]} />
          <Text style={styles.legendText}>Magnitud ≥ 7</Text>
        </View>
        <View style={styles.legendItem}>
          <View style={[styles.legendCircle, {backgroundColor: "#ff8800"}]} />
          <Text style={styles.legendText}>Magnitud 6-6.9</Text>
        </View>
        <View style={styles.legendItem}>
          <View style={[styles.legendCircle, {backgroundColor: "#ffff00"}]} />
          <Text style={styles.legendText}>Magnitud 5-5.9</Text>
        </View>
      </View>
      <Text style={styles.totalText}>Total de sismos mostrados: {sismos.length}</Text>
      <View style={styles.mapContainer}>
        <MapView
          style={styles.map}
          initialCamera={{
            center: {
              latitude: 23.6345,
              longitude: -102.5528,
            },
            zoom: 5,
            pitch: 0,
            heading: 0,
            altitude: 0,
          }}
        >
          {sismos.map((sismo, idx) => (
            <Marker
              key={idx}
              coordinate={{ latitude: sismo.latitud, longitude: sismo.longitud }}
              pinColor={colorByMagnitud(sismo.magnitud)}
              onPress={() => setSelectedSismo(sismo)}
            >
              {/* Puedes personalizar el icono aquí si lo deseas */}
            </Marker>
          ))}
          {selectedSismo && (
            <Circle
              center={{ latitude: selectedSismo.latitud, longitude: selectedSismo.longitud }}
              radius={20000}
              strokeColor="rgba(66,133,244,0.4)"
              fillColor="rgba(66,133,244,0.1)"
            />
          )}
        </MapView>
      </View>
      {selectedSismo && (
        <View style={styles.infoBox}>
          <Text style={styles.infoTitle}>Sismo {selectedSismo.magnitud || "No calculable"}</Text>
          <Text style={styles.infoText}><Text style={styles.infoLabel}>Fecha:</Text> {selectedSismo.fecha}</Text>
          <Text style={styles.infoText}><Text style={styles.infoLabel}>Hora:</Text> {selectedSismo.hora}</Text>
          <Text style={styles.infoText}><Text style={styles.infoLabel}>Profundidad:</Text> {selectedSismo.profundidad} km</Text>
          <Text style={styles.infoText}><Text style={styles.infoLabel}>Ubicación:</Text> {selectedSismo.referenciaLocalizacion}</Text>
          <Text style={styles.infoText}><Text style={styles.infoLabel}>Coordenadas:</Text> {selectedSismo.latitud}, {selectedSismo.longitud}</Text>
          <TouchableOpacity style={styles.button} onPress={() => navigation.navigate('SismoPropagation', { sismo: selectedSismo })}>
            <Text style={styles.buttonText}>Ver Propagación de Ondas</Text>
          </TouchableOpacity>
        </View>
      )}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { paddingBottom: 30, backgroundColor: '#fff', alignItems: 'center', flexGrow: 1 },
  title: { fontSize: 22, color: '#FF9200', marginVertical: 16, fontWeight: 'bold' },
  legendRow: { flexDirection: 'row', gap: 16, marginBottom: 8 },
  legendItem: { flexDirection: 'row', alignItems: 'center', marginRight: 12 },
  legendCircle: { width: 18, height: 18, borderRadius: 12, marginRight: 6, borderWidth: 1, borderColor: '#999' },
  legendText: { fontSize: 13, color: '#333' },
  totalText: { fontSize: 14, marginBottom: 4, color: '#555' },
  mapContainer: { width: Dimensions.get('window').width * 0.96, height: 370, borderRadius: 14, overflow: 'hidden', marginBottom: 20 },
  map: { width: '100%', height: '100%' },
  loaderBox: { flex: 1, alignItems: 'center', justifyContent: 'center', marginTop: 40 },
  infoBox: { padding: 16, backgroundColor: '#f9fafb', borderRadius: 12, marginTop: 8, width: '94%', alignSelf: 'center', elevation: 2 },
  infoTitle: { fontWeight: 'bold', fontSize: 17, marginBottom: 6, color: '#2563eb' },
  infoText: { fontSize: 14, marginBottom: 2, color: '#222' },
  infoLabel: { fontWeight: 'bold', color: '#333' },
  button: { marginTop: 12, backgroundColor: '#4285f4', paddingVertical: 10, borderRadius: 8, alignItems: 'center' },
  buttonText: { color: '#fff', fontWeight: 'bold', fontSize: 15 }
});

export default SismosMapScreen;