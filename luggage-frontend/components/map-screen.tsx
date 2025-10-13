//Our main map view component. This can be imported and placed on any page. 
//TODO: Use the newly acquired Google Maps API Key | MAY NEED TO BE DEPLOYED TO GOOGLE APP STORE AT LEAST ONCE 
//TODO: Communicate with backend to place pins of hosts
//TODO: Rerequest location permission from user if they declined. Nothing happens if declined currently. 

import React from 'react';
import { useState, useEffect } from 'react';
import { StyleSheet, View, Button, Alert, BackHandler } from 'react-native';
import MapView, { Marker, Region } from 'react-native-maps';
import * as Location from 'expo-location';
import { Platform } from 'react-native';


/**
 * Displays a map centered on the user's current location.
 *
 * - Requests location permissions and fetches the user's current position.
 * - Shows a marker at the user's location.
 * - Displays an error message if location permission is denied.
 * - Uses Expo Location API and React Native Maps.
 *
 * @returns A React component rendering a map with the user's location marker.
 */
const LocationDeniedAlert = () =>
  Alert.alert('Error', 'You need to allow APP to access your location to operate correctly!', [
    {
      onPress: () => BackHandler.exitApp(),
      style: 'cancel'
    },
    {
      text: 'Allow',
      onPress: () => console.log('Allow pressed'),
    }
  ]);

export default function MapScreen() {
  const [location, setLocation] = useState<Location.LocationObject | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [region, setRegion] = useState<Region | undefined>(undefined)

  useEffect(() => {
    async function getCurrentLocation() {

      let { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== 'granted') {
        setErrorMsg('Permission to access location was denied');
        LocationDeniedAlert();
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
    <View style={styles.root}>
      <MapView
        style={StyleSheet.absoluteFillObject}
        region={region}
        showsUserLocation={true}
        {...(Platform.OS === 'ios' ? { followsUserLocation: true } : {})}
      >
        {region && (
          <Marker
            coordinate={region}
          />
        )}
      </MapView>
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    width: '100%',
    height: '100%',
  },
});