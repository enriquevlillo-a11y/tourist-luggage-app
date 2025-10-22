import React, { useMemo } from "react";
import {

Platform,
View,
Text,
StyleSheet,
useWindowDimensions,
ViewStyle,
} from "react-native";

interface MapScreenProps {
latitude?: number;
longitude?: number;
zoom?: number; // approximate, used to compute bbox for OSM iframe
height?: number | string;
style?: ViewStyle;
}

/**
 * Web-first map component for react-native-web projects.
 * Renders an OpenStreetMap embed iframe on web and a placeholder on native platforms.
 *
 * Notes:
 * - This is intentionally dependency-free for web embeds. For more control on native,
 *   replace the native placeholder with react-native-maps.
 */
export default function MapScreenWeb({
latitude = 37.7749,
longitude = -122.4194,
zoom = 12,
height = 400,
style,
}: MapScreenProps) {
const { width: windowWidth } = useWindowDimensions();

const iframeSrc = useMemo(() => {
    // Compute a small bbox around the center. The delta decreases as "zoom" increases.
    const delta = Math.max(0.002, 0.05 / Math.max(1, zoom));
    const south = latitude - delta;
    const north = latitude + delta;
    const west = longitude - delta * (windowWidth / Math.max(windowWidth, 320));
    const east = longitude + delta * (windowWidth / Math.max(windowWidth, 320));

    // OpenStreetMap embed URL with a marker
    const marker = `${latitude}%2C${longitude}`;
    const bbox = `${west}%2C${south}%2C${east}%2C${north}`;
    return `https://www.openstreetmap.org/export/embed.html?bbox=${bbox}&layer=mapnik&marker=${marker}`;
}, [latitude, longitude, zoom, windowWidth]);

if (Platform.OS === "web") {
    return (
        <View style={[styles.container, style]}>
            <iframe
                title="map"
                src={iframeSrc}
                style={{ border: 0, width: "100%", height }}
                loading="lazy"
                referrerPolicy="no-referrer-when-downgrade"
            />
        </View>
    );
}

// Native placeholder: swap this with react-native-maps MapView for iOS/Android
return (
    <View style={[styles.nativePlaceholder, style]}>
        <Text style={styles.placeholderText}>
            Map (native) placeholder. Install and use react-native-maps for native maps.
        </Text>
        <Text style={styles.smallText}>
            Center: {latitude.toFixed(5)}, {longitude.toFixed(5)} • Zoom: {zoom}
        </Text>
    </View>
);
}

const styles = StyleSheet.create({
container: {
    width: "100%",
    overflow: "hidden",
    borderRadius: 8,
    backgroundColor: "#eee",
},
nativePlaceholder: {
    width: "100%",
    height: 200,
    borderRadius: 8,
    backgroundColor: "#f2f2f2",
    justifyContent: "center",
    alignItems: "center",
    padding: 12,
},
placeholderText: {
    fontSize: 14,
    color: "#333",
    textAlign: "center",
    marginBottom: 6,
},
smallText: {
    fontSize: 12,
    color: "#666",
},
});