//This page shows all the details and features a particular spot has such as the price, hours, location and so on. 
//

import { useLocalSearchParams, useNavigation } from "expo-router";
import { View, Text, Button, StyleSheet } from "react-native";
import { useEffect, useState } from 'react';
import { useSpotsStore } from "../../stores/spots";
import StarRating from "react-native-star-rating-widget";
import { LinearGradient } from "expo-linear-gradient";

export default function SpotDetail() {
  const navigation = useNavigation();
  const { id } = useLocalSearchParams<{ id: string }>();
  const spot = useSpotsStore((s) => s.getById(Number(id)));
  const [rating, setRating] = useState(spot?.rating ?? 0);


  //Simple text if we can't find the spot by its id. 
  if (!spot) {
    return <View style={{ padding: 16 }}><Text>Spot not found!</Text></View>;
  }

  //Override the default header title: 'Home'
  useEffect(() => {
    navigation.setOptions({ title: "Reserve Spot" });
  }, [navigation]);

  return (
    <View style={styles.root}>
      <LinearGradient
        colors={['#f9f9f9', '#2476B488']} start={{ x: 0, y: 0 }}
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
          <Text style={styles.hoursTitle}>Hours of Operation</Text>
        </View>
      </LinearGradient>
    </View>
  );
}

const styles = StyleSheet.create({
  root: { padding: 16, gap: 12 },

  card: {
    borderRadius: 16,
    padding: 16,
    overflow: 'hidden',         // ensures rounded corners clip the gradient
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 8,
    elevation: 3,
  },

  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: 12,
  },

  leftCol: { flex: 1, minWidth: 0 },

  ctaCol: {
    width: 140,
    alignItems: 'center',
    gap: 6,
  },

  reserveSpotTitle: {
    fontFamily: 'Roboto',
    fontSize: 26,
    fontWeight: '700',
    letterSpacing: 0.5,
    color: '#222',
    marginBottom: 6,
  },

  ratingRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    left: -8
  },

  ratingText: { 
    fontSize: 14, 
    color: '#555' },

  priceText: {
    fontSize: 18,
    fontWeight: '700',
    color: '#2E7D32',
    marginTop: 4,
    textAlign: 'center',
  },

  priceUnit: {
    fontSize: 14,
    color: '#555',
    fontWeight: '600'
  },

  details: {
    marginTop: 12
  },

  hoursTitle: {
    fontSize: 20,
    fontWeight: '600',
    color: '#444'
  },
});
