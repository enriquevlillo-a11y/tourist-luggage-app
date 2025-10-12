//This is the entry point for the program. 

import { Text, View, StyleSheet } from "react-native";
import MapScreen from '@/components/map-screen'

export default function Index() {
  return (
    <View style={styles.root}>
      <MapScreen />
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 }, // full screen container
  overlay: {
    position: "absolute",
    top: 50,
    alignSelf: "center",
    backgroundColor: "rgba(0,0,0,0.6)",
    color: "white",
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 8,
  },
});