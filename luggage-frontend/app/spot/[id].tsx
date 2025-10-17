// SpotDetail.tsx
import { useLocalSearchParams, useNavigation } from "expo-router";
import { View, Text, Button, StyleSheet, ScrollView } from "react-native";
import { useEffect, useState } from "react";
import { useSpotsStore } from "../../stores/spots";
import StarRating from "react-native-star-rating-widget";
import { LinearGradient } from "expo-linear-gradient";
import MapView, { Marker } from "react-native-maps";

export default function SpotDetail() {
  const navigation = useNavigation();
  const { id } = useLocalSearchParams<{ id: string }>();
  const spot = useSpotsStore((s) => s.getById(Number(id)));
  const [rating, setRating] = useState(spot?.rating ?? 0);

  if (!spot) {
    return (
      <View style={{ padding: 16 }}>
        <Text>Spot not found!</Text>
      </View>
    );
  }

  useEffect(() => {
    setRating(spot?.rating ?? 0);
  }, [spot?.rating]);

  useEffect(() => {
    navigation.setOptions({ title: "Reserve Spot" });
  }, [navigation]);

  const region = {
    latitude: spot.lat,
    longitude: spot.long,
    latitudeDelta: 0.012,  // tighter zoom than default
    longitudeDelta: 0.008,
  };

  return (
    <View style={styles.root}>
      {/* TOP: Map */}
      <View style={styles.mapWrapper}>
        <MapView style={styles.map} initialRegion={region}>
          <Marker coordinate={{ latitude: spot.lat, longitude: spot.long }} title={spot.name} />
        </MapView>
      </View>

      {/* BOTTOM: Scrollable details */}
      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <LinearGradient
          colors={["#F8FAFC", "#4288ceff"]}
          start={{ x: 0, y: 0 }}
          end={{ x: 0, y: 1 }}
          style={styles.card}
        >
          {/* Header row */}
          <View style={styles.headerRow}>
            {/* Left: Name + Rating */}
            <View style={styles.leftCol}>
              <Text style={styles.reserveSpotTitle}>{spot.name}</Text>

              <View style={styles.ratingRow}>
                <StarRating
                  starSize={22}
                  rating={rating}
                  onChange={setRating}
                  enableHalfStar
                />
                <Text style={styles.ratingText}>{rating.toFixed(1)}</Text>
              </View>
            </View>

            {/* Right: Book + Price underneath */}
            <View style={styles.ctaCol}>
              <Button title="Book" onPress={() => { /* your action */ }} />
              <Text style={styles.priceText}>
                ${spot.price}
                <Text style={styles.priceUnit}> /day</Text>
              </Text>
            </View>
          </View>

          {/* Address / details */}
          <View style={styles.details}>
            <Text style={styles.subtleLabel}>Address</Text>
            <Text style={styles.address}>{spot.address}</Text>
          </View>

          {/* Hours block (example) */}
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Hours of Operation</Text>
            <Text style={styles.sectionText}>Mon–Sun · 8:00 AM – 10:00 PM</Text>
          </View>

          {/* Add more sections here... */}
          <Text style={styles.sectionTitle}>Reviews</Text>
        </LinearGradient>

        {/* Spacer so it feels breathable when scrolled to bottom */}
        <View style={{ height: 24 }} />
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: "#F2F4F7",
  },

  // MAP
  mapWrapper: {
    height: 260,               // nice visual height; tweak as you like
    backgroundColor: "#e6ecf2",
  },
  map: {
    flex: 1,
  },

  // SCROLL
  scroll: {
    flex: 1,
  },
  scrollContent: {
    padding: 16,
    paddingTop: 12,
  },

  // CARD
  card: {
    borderRadius: 16,
    padding: 16,
    overflow: "hidden", // ensures rounded corners clip the gradient
    shadowColor: "#000",
    shadowOpacity: 0.05,
    shadowRadius: 8,
    elevation: 3,
  },

  headerRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    gap: 12,
  },

  leftCol: {
    flex: 1,
    minWidth: 0
  },

  ctaCol: {
    width: 140,
    alignItems: "center",
    gap: 6,
  },

  reserveSpotTitle: {
    fontFamily: "Roboto",
    fontSize: 26,
    fontWeight: "700",
    letterSpacing: 0.5,
    color: "#222",
    marginBottom: 6,
  },

  ratingRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    left: -8,
  },

  ratingText: {
    fontSize: 14,
    color: "#555",
  },

  priceText: {
    fontSize: 18,
    fontWeight: "700",
    color: "#2E7D32",
    marginTop: 4,
    textAlign: "center",
  },

  priceUnit: {
    fontSize: 14,
    color: "#555",
    fontWeight: "600",
  },

  details: {
    marginTop: 12,
  },

  subtleLabel: {
    fontSize: 12,
    color: "#6b7280",
    marginBottom: 4,
    textTransform: "uppercase",
    letterSpacing: 0.6,
  },

  address: {
    fontSize: 15,
    color: "#444",
  },

  section: {
    marginTop: 16,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: "600",
    color: "#444",
    marginBottom: 6,
  },
  sectionText: {
    fontSize: 15,
    color: "#444",
    lineHeight: 20,
  },
});
