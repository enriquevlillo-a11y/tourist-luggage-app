import { useLocalSearchParams, Stack, useNavigation } from "expo-router";
import { View, Text, Button, StyleSheet } from "react-native";
import { useEffect } from 'react';
import { useSpotsStore } from "../../stores/spots";

//By default, the navigator defined in _layout defines the title for each page as Home, however, we can use navigation.setOptions to override 
//some header options such as the title. 
export default function SpotDetail() {
  const navigation = useNavigation();
  const { id } = useLocalSearchParams<{ id: string }>();
  const spot = useSpotsStore((s) => s.getById(Number(id)));

  if(!spot) {
    return <View style={{padding:16}}><Text>Spot not found!</Text></View>;
  }

  useEffect(() => {
    navigation.setOptions({
      title: `Reserve Spot`,
    })
  })

  return (
    <View style={styles.root}>
      <Text>{spot.name}</Text>
      <Text>{spot.address}</Text>
      <Text>{spot.address}</Text>
      <Button title="Reservar" onPress={() => { }} />
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    padding: 16,
    gap: 12
  }
})