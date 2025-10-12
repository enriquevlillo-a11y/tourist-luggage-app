//Our main map view component. This can be imported and placed on any page. 
//TODO: Use the newly acquired Google Maps API Key | MAY NEED TO BE DEPLOYED TO GOOGLE APP STORE AT LEAST ONCE 
//TODO: Communicate with backend to place pins of hosts
//TODO: Change the default location to the users', preferably.  UPDATE: Done but the android emulator defaults to Google HQ. On an actual device, it grabs the location but does NOT change the view to reflect this. 

import React from 'react';
import { Text } from '@react-navigation/elements';
import { useState, useEffect } from 'react';
import { StyleSheet, View } from 'react-native';
import MapView, { Marker, Region } from 'react-native-maps';
import * as Location from 'expo-location';


export default function MapScreen() {
  const [location, setLocation] = useState<Location.LocationObject | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [region, setRegion] = useState<Region | undefined>(undefined)

  useEffect(() => {
    async function getCurrentLocation() {

      let { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== 'granted') {
        setErrorMsg('Permission to access location was denied');
        return;
      }

      let location = await Location.getCurrentPositionAsync({});
      setLocation(location);


      setRegion({
        latitude: location.coords.latitude,
        longitude: location.coords.longitude,
        latitudeDelta: 0.05,
        longitudeDelta: 0.05
      })
    }

    getCurrentLocation();
  }, []);

  let text = 'Waiting...';
  if (errorMsg) {
    text = errorMsg;
  } else if (location) {
    text = JSON.stringify(location);
  }

  return (
    <View style={styles.container}>
      <MapView
        style={styles.map}
        initialRegion={region}
        showsUserLocation={true}
        followsUserLocation={false}
      >
        {region && (
          <Marker
            coordinate={region}
          />
        )}
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
