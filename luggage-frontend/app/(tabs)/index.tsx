//Our home page. 

import { useState, useEffect } from "react";
import {
  View,
  Text,
  FlatList,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  TouchableWithoutFeedback,
  Keyboard
} from "react-native";
import { Link } from "expo-router";
import MapScreen from '../../components/map-screen'
import { useSpotsStore } from "../../stores/spots";


//TODO: Grab list of available hosts from backend
//TODO: The list of available hosts should be based on the current map location and should be within a specified radius. \

// const MOCK = [
//   { id: 1, name: "Locker Center", price: 6, address: "Brickell Ave", rating: 4.5, lat: 25.7617, long: -80.1918, reviews: [] },
//   { id: 2, name: "Hotel Plaza", price: 8, address: "Downtown", rating: 4.2, lat: 25.7736, long: -80.1937, reviews: [] },
//   { id: 3, name: "Cafe Storage", price: 5, address: "Wynwood", rating: 4.0, lat: 25.8007, long: -80.1994, reviews: [] },
//   { id: 4, name: "Bayfront Lockers", price: 7, address: "Biscayne Blvd", rating: 4.3, lat: 25.7825, long: -80.1856, reviews: [] },
//   { id: 5, name: "Airport Storage Hub", price: 10, address: "Miami International Airport", rating: 4.7, lat: 25.7959, long: -80.2870, reviews: [] },
// ];




export default function Home() {
  const [q, setQ] = useState("");
  // const data = MOCK.filter(s => s.name.toLowerCase().includes(q.toLowerCase()));
  const data = useSpotsStore((s) => s.spots);
  // useEffect(() => {
  //   useSpotsStore.getState().setSpots(MOCK);
  // }, [])

  return (
    <View style={styles.root}>

      {/* Map & Search Container*/}

      <View style={styles.map}>
        <TextInput
          placeholder="Search"
          value={q}
          onChangeText={setQ}
          style={styles.searchBar}
        />
        <MapScreen />
      </View>

      {/* List of location cards */}

      <FlatList
        contentContainerStyle={styles.cardList}
        data={data}
        keyExtractor={(it) => String(it.id)}
        renderItem={({ item }) => (
          <Link href={`/spot/${item.id}`} asChild>
            <TouchableOpacity style={styles.card}>
              <Text style={{ fontWeight: "700", fontSize: 16 }}>{item.name}</Text>
              <Text style={{ color: "#6B7280", marginTop: 4 }}>${item.price}/h</Text>
              <Text style={{ color: "#9CA3AF", marginTop: 4 }}>{item.address}</Text>
            </TouchableOpacity>
          </Link>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
  },
  cardList: {
    paddingHorizontal: 16,
    paddingBottom: 20
  },
  card: {
    backgroundColor: "#fff",
    borderRadius: 16,
    padding: 16,
    marginVertical: 8,
    elevation: 2
  },
  searchBar: {
    backgroundColor: "rgba(255, 255, 255, 0.5)",
    top: 10,
    left: 10,
    position: 'absolute',
    width: "75%",
    padding: 12,
    borderRadius: 12,
    borderWidth: 1,
    fontSize: 16,
    zIndex: 10
  },
  map: {
    margin: 16,
    height: 350,
    borderRadius: 60,
    justifyContent: "center",
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#E5E7EB",
    position: 'relative'
  }
})