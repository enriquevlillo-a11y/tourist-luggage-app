import { useLocalSearchParams } from "expo-router";
import { View, Text, Button } from "react-native";

export default function SpotDetail() {
  const { id } = useLocalSearchParams<{id:string}>();
  return (
    <View style={{ flex:1, padding:16, gap:12 }}>
      <Text style={{ fontSize:22, fontWeight:"800" }}>Spot #{id}</Text>
      <Text>Detalle del lugar y botón de reserva</Text>
      <Button title="Reservar" onPress={()=>{}} />
    </View>
  );
}