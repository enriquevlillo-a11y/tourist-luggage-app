import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { useRouter } from "expo-router";
import {
  SafeAreaView,
  SafeAreaProvider,
} from 'react-native-safe-area-context';

import { Calendar, CalendarList, Agenda } from "react-native-calendars";
import { useState } from "react";

export default function TemplateModal() {
  const router = useRouter();
  const [selected, setSelected] = useState('');
  return (
    <SafeAreaProvider>
      <SafeAreaView style={styles.container}>

        {/* Header */}
        <Calendar onDayPress={day => {
          setSelected(day.dateString);
          console.log('selected day', day);
        }}/>

        {/* Body */}
        <View style={styles.body}>
          <Text style={styles.text}>This is where the prices will be displayed.</Text>
        </View>

        {/* Footer / Buttons */}
        <View style={styles.footer}>
          <TouchableOpacity
            style={[styles.button, { backgroundColor: "#2563eb" }]}
            onPress={() => console.log("Confirm action")}
          >
            <Text style={{ color: "#fff", fontWeight: "600" }}>Pay with Stripe</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.button, { backgroundColor: "#e5e7eb" }]}
            onPress={() => console.log("Other payment method")}
          >
            <Text style={{ fontWeight: "600" }}>Pay with other method</Text>
          </TouchableOpacity>

        </View>
      </SafeAreaView>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
  },
  title: {
    fontSize: 20,
    fontWeight: "700",
    marginBottom: 12,
    textAlign: "center",
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
    flexDirection: "column",
    justifyContent: "space-between",
    padding: 16,
  },
  button: {
    padding: 12,
    borderRadius: 8,
    alignItems: "center",
    marginHorizontal: 4,
  },
});
