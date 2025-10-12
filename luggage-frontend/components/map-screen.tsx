//Our main map view component. This can be imported and placed on any page. 
//TODO: Use the newly acquired Google Maps API Key | MAY NEED TO BE DEPLOYED TO GOOGLE APP STORE AT LEAST ONCE 
//TODO: Communicate with backend to place pins of hosts
//TODO: Change the default location to the users', preferably. 

import React from 'react';
import { StyleSheet, View } from 'react-native';
import MapView, { Marker } from 'react-native-maps';

export default function MapScreen() {
  return (
    <View style={styles.container}>
      <MapView
        style={styles.map}
        initialRegion={{
          latitude: 25.7617,     // Miami
          longitude: -80.1918,
          latitudeDelta: 0.0922,
          longitudeDelta: 0.0421,
        }}
      >
        <Marker
          coordinate={{ latitude: 25.7617, longitude: -80.1918 }}
          title="Miami"
          description="This is a marker in Miami."
        />
      </MapView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  map: {
    width: '100%',
    height: '100%',
  },
});
