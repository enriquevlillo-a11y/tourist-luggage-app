//Our home page. 

import { useState, useEffect } from "react";
import {
  View,
  Text,
  FlatList,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Keyboard,
  Platform
} from "react-native";
import { Link } from "expo-router";
import MapScreen from '../../components/map-screen'
import { useSpotsStore } from "../../stores/spots";


//TODO: Grab list of available hosts from backend
//TODO: The list of available hosts should be based on the current map location and should be within a specified radius. \

//Pick the correct base url depending on the platform
function getApiBase () {
  if (Platform.OS === "android") return "http://10.0.2.2:8081";
  return "http://localhost:8081";
}

export default function Home() {
  const [q, setQ] = useState("");
  const { spots, setSpots } = useSpotsStore();


  //Fetch spots from backend when component mounts
  // and store them in Zustand store
  useEffect(() => {
    const base = getApiBase();
    async function fetchSpots() {
      try {
        const res = await fetch(`${base}/spots?lat=25.7617&lng=-80.1918&radiusMeters=3000`);
        const data = await res.json();
        setSpots(data); // updates Zustand store
      } catch (err) {
        console.error("Failed to fetch spots:", err);
        console.log({base});
      }
    }

    fetchSpots();
  }, [setSpots]); // runs once when Home mounts

  //Filter spots based on search query
  const data = spots.filter(
    (spot) => spot.name.toLowerCase().includes(q.toLowerCase())
  );

  return (
    <View style={styles.root}>

      {/* Map & Search Container*/}

      <View style={styles.map}>
        <TextInput
          placeholder="Search"
          value={q}
          onChangeText={setQ}
          style={styles.searchBar}
          returnKeyType="search"
          onSubmitEditing={Keyboard.dismiss}
        />
        <MapScreen />
      </View>

      {/* List of location cards */}

      <FlatList
        contentContainerStyle={styles.cardList}
        data={data}
        keyExtractor={(it, idx) => it.id ? String(it.id) : `spot-${idx}`}
        renderItem={({ item }) => (
          <Link href={`/spot/${item.id}`} asChild>
            <TouchableOpacity style={styles.card}>
              <Text style={{ fontWeight: "700", fontSize: 16 }}>{item.name}</Text>
              <Text style={{ color: "#6B7280", marginTop: 4 }}>${item.price.toFixed(2)}/h</Text>
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