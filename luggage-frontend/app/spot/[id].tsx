/**
 * SpotDetail
 *
 * React Native / Expo screen component that displays details for a single "spot"
 *  identified by a route param.
 *
 * Behavior
 * - Reads the `id` route parameter via `useLocalSearchParams<{ id: string }>()`
 *   and looks up the spot using `useSpotsStore((s) => s.getById(Number(id)))`.
 * - If no spot is found, renders a simple "Spot not found!" fallback view.
 * - Maintains local UI state for the star rating (`rating`) initialized from
 *   `spot?.rating` and kept in sync via an effect:
 *     useEffect(() => setRating(spot?.rating ?? 0), [spot?.rating])
 * - Sets the navigation bar title to "Reserve Spot" via:
 *     navigation.setOptions({ title: "Reserve Spot" })
 *
 * UI composition
 * - Top: a MapView centered on the spot's coordinates with a Marker.
 *   The map is given an `initialRegion` constructed from:
 *     { latitude: spot.lat, longitude: spot.long, latitudeDelta, longitudeDelta }
 *   Note: `initialRegion` is used; to animate or reactively update the map
 *   when the spot changes, consider using the `region` prop or imperative refs.
 * - Card (LinearGradient) containing:
 *   - Header row with:
 *     - Left column: spot name and a StarRating component bound to local `rating`.
 *     - Right column: "Book" Button and price display (formatted as `$<price> /day`).
 *   - Details block: Address label + address text.
 *   - Hours of Operation example block (static text by default).
 *   - Reviews: a FlatList rendering `spot.reviews` with each item showing comment,
 *     optional user attribution, and optional numeric rating.
 *
 * Data expectations (shape of `spot`)
 * - id: number | string
 * - name: string
 * - lat: number
 * - long: number
 * - rating?: number
 * - price?: number
 * - address?: string
 * - reviews?: Array<{
 *     id?: number | string;
 *     comment?: string;
 *     user?: string;
 *     rating?: number;
 *   }>
 *
 * Accessibility & UX notes
 * - Ensure the StarRating control is accessible (labeling / focus) if required.
 * - The "Book" Button handler is a placeholder; wire navigation or booking logic
 *   to integrate with the app's reservation flow.
 * - When rendering user-generated content (reviews/comments), sanitize or escape
 *   strings as appropriate for your platform to avoid injection-like issues.
 *
 * Performance & robustness recommendations
 * - Parse and validate the `id` route param before lookup (currently cast with Number).
 * - Guard all spot fields that may be undefined before use (map coordinates, price).
 * - Consider memoizing derived values (e.g. region) if the component becomes more complex.
 * - If many reviews are expected, tune FlatList props (initialNumToRender, maxToRenderPerBatch)
 *   and provide stable keys for list items.
 *
 * File path / route
 * - Route pattern: /spot/[id]
 *
 * Side effects
 * - Calls `navigation.setOptions` to set the screen title.
 * - Calls `setRating` effect when the spot's rating changes.
 *
 * Example
 * - Navigating to this screen with id=42 should render the details for the spot
 *   returned by `useSpotsStore().getById(42)`.
 */
// SpotDetail.tsx
import { Link, useLocalSearchParams, useNavigation, usePathname } from "expo-router";
import { View, Text, Button, StyleSheet, FlatList, TouchableOpacity } from "react-native";
import { useEffect, useState } from "react";
import { useSpotsStore } from "../../stores/spots";
import StarRating from "react-native-star-rating-widget";
import { LinearGradient } from "expo-linear-gradient";
import MapView, { Marker } from "react-native-maps";
import { useRouter } from "expo-router";


export function ReserveButton({ id }: { id: number | string }) {
  const router = useRouter();
  return (
    <Link href={`/(modals)/reserve?spotId=${id}`} asChild>
      <TouchableOpacity style={styles.ctaCol}>
        <Text style={styles.button}>Reserve</Text>
      </TouchableOpacity>
    </Link>
  );
}

export default function SpotDetail() {
  const pathname = usePathname();
  const { id } = useLocalSearchParams<{ id?: string }>();

  // Call hooks unconditionally to satisfy the Rules of Hooks.
  // Use a safe fallback for id when passing to the store selector.
  const spot = useSpotsStore((s) => s.getById(String(id ?? "")));
  const [rating, setRating] = useState<number>(spot?.rating ?? 0);

  useEffect(() => {
    setRating(spot?.rating ?? 0);
    console.log(pathname);
  }, [spot?.rating, pathname]);

  if (!id) {
    console.error("No id provided in route params");
    return (
      <View style={{ padding: 16 }}>
        <Text>No spot selected</Text>
      </View>
    );
  }

  if (!spot) {
    return (
      <View style={{ padding: 16 }}>
        <Text>Spot not found!</Text>
      </View>
    );
  }

  const region = {
    latitude: spot.latitude,
    longitude: spot.longitude,
    latitudeDelta: 0.012,  // tighter zoom than default
    longitudeDelta: 0.008,
  };

  return (
    <View style={styles.root}>
      {/* TOP: Map */}
      <View style={styles.mapWrapper}>
        <MapView style={styles.map} initialRegion={region}>
          <Marker coordinate={{ latitude: spot.latitude, longitude: spot.longitude }} title={spot.name} />
        </MapView>
      </View>

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
            <ReserveButton id={spot.id} />
            <Text style={styles.priceText}>
              ${spot.pricePerHour}
              <Text style={styles.priceUnit}> /hour</Text>
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
        <FlatList
          data={spot.reviews ?? []}
          renderItem={({ item }) => (
            <View style={{ paddingVertical: 8 }}>
              <Text style={styles.sectionText}>
                {item?.comment ?? String(item)}
              </Text>
              <Text style={{ fontSize: 13, color: "#6b7280", marginTop: 4 }}>
                {item?.user ? `— ${item.user}` : null}
                {item?.rating ? ` (${item.rating.toFixed(1)} ⭐)` : null}
              </Text>
            </View>
          )}
          keyExtractor={(item, index) => (item?.id ? String(item.id) : String(index))}
        />
      </LinearGradient>

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
  button: {
    backgroundColor: "#678bd8ff",          // blue-600
    paddingVertical: 14,
    paddingHorizontal: 24,
    borderRadius: 12,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 3,
    elevation: 4,                        // Android shadow
    alignItems: "center",
  },
  card: {
    borderRadius: 16,
    padding: 16,
    overflow: "hidden", // ensures rounded corners clip the gradient
    shadowColor: "#000",
    shadowOpacity: 0.05,
    shadowRadius: 8,
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
