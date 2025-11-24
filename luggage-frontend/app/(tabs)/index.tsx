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
function getApiBase() {
  if (Platform.OS === "android") return "http://10.0.2.2:8081";
  if (Platform.OS === "web") return "http://localhost:8081";
  if (Platform.OS === "ios") return "http://localhost:8081";
  return "http://localhost:8081";
}


async function fetchLocations() {
  const base = getApiBase();
  try {
    const res = await fetch(`${base}/api/locations`);
    if (!res.ok) {
      throw new Error("Failed to fetch locations");
    }
    const data = await res.json();

    // Backend returns paginated response with 'content' array
    const locations = data.content || data;

    const normalized = locations.map((location: any) => ({
      id: String(location.id),
      name: location.name,
      pricePerHour: Number(location.pricePerHour),
      address: location.address,
      rating: Number(location.rating ?? 0),
      latitude: Number(location.latitude),
      longitude: Number(location.longitude),
      reviews: location.reviews ?? [],
    }));

    useSpotsStore.getState().setSpots(normalized);
  } catch (error) {
    console.error("Error fetching locations:", error);
    // Log more details for debugging
    if (error instanceof Error) {
      console.error("Error details:", error.message);
    }
  }
}


export default function Home() {
  const [q, setQ] = useState("");
  const { locations: spots } = useSpotsStore();


  useEffect(() => {
    fetchLocations();
  }, []);

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
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <Link href={{pathname: "/spot/[id]", params: {id: String(item.id)}}} asChild>
            <TouchableOpacity style={styles.card} onPress={() => {
              console.log(item.id);
            }}>
              <Text style={{ fontWeight: "700", fontSize: 16 }}>{item.name}</Text>
              <Text style={{ color: "#6B7280", marginTop: 4 }}>${item.pricePerHour?.toFixed(2)}/h</Text>
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