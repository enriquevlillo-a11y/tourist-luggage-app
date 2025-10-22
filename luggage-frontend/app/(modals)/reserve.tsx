import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { useRouter } from "expo-router";

export default function TemplateModal() {
  const router = useRouter();

  return (
    <View style={styles.container}>
      {/* Header */}
      <Text style={styles.title}>Modal Title</Text>

      {/* Body */}
      <View style={styles.body}>
        <Text style={styles.text}>This is an empty modal template.</Text>
      </View>

      {/* Footer / Buttons */}
      <View style={styles.footer}>
        <TouchableOpacity
          style={[styles.button, { backgroundColor: "#e5e7eb" }]}
          onPress={() => router.back()}
        >
          <Text style={{ fontWeight: "600" }}>Cancel</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.button, { backgroundColor: "#2563eb" }]}
          onPress={() => console.log("Confirm action")}
        >
          <Text style={{ color: "#fff", fontWeight: "600" }}>Confirm</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    justifyContent: "space-between",
  },
  title: {
    fontSize: 20,
    fontWeight: "700",
    marginBottom: 12,
  },
  body: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  text: {
    color: "#6b7280",
  },
  footer: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginTop: 24,
  },
  button: {
    flex: 1,
    padding: 12,
    borderRadius: 8,
    alignItems: "center",
    marginHorizontal: 4,
  },
});
