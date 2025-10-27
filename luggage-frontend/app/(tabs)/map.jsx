import { View, Text } from "react-native";
import MapScreen from '../../components/map-screen'

export default function Map() {
  return (
    <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
      <MapScreen />
    </View>
  );
}