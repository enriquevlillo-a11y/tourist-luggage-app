import { View, Text, StyleSheet, TouchableOpacity, Switch, useWindowDimensions } from "react-native";
import {
  SafeAreaView,
  SafeAreaProvider,
} from 'react-native-safe-area-context';

import { Calendar } from "react-native-calendars";
import { useState } from "react";
import { useSpotsStore } from "../../stores/spots";
import { useLocalSearchParams } from "expo-router";

//TODO: Is it price per hour or per day?


export function ModeSwitch({ mode, setMode }: { mode: "hourly" | "daily", setMode: (m: "hourly" | "daily") => void }) {
  const { width } = useWindowDimensions();
  return (
    <View style={[styles.container, ]}>
      <TouchableOpacity
        style={[styles.option, mode === "daily" && styles.active]}
        onPress={() => setMode("daily")}
      >
        <Text style={[styles.label, mode === "daily" && styles.activeLabel]}>Daily</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={[styles.option, mode === "hourly" && styles.active]}
        onPress={() => setMode("hourly")}
      >
        <Text style={[styles.label, mode === "hourly" && styles.activeLabel]}>Hourly</Text>
      </TouchableOpacity>
    </View>
  );
}
export default function TemplateModal() {

  const { locationId } = useLocalSearchParams<{ locationId: string }>();
  const location = useSpotsStore(s => s.getById(String(locationId)));
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  const [mode, setMode] = useState<"hourly" | "daily">("hourly");


  if (!location) {
    return (
      <Text>Location not found</Text>
    )
  }

  return (
    <SafeAreaProvider>
      <SafeAreaView style={styles.container}>
        <ModeSwitch mode={mode} setMode={setMode} />
        {/* Header */}
        <Calendar onDayPress={day => {
          if (!fromDate || (fromDate && toDate)) {
            setFromDate(day.dateString);
            setToDate('');
          } else if (fromDate && !toDate) {
            if (day.dateString > fromDate) {
              setToDate(day.dateString);
            } else {
              // If selected date is before or same as fromDate, start new selection
              setFromDate(day.dateString);
              setToDate('');
            }
          }
          console.log('selected day', day);
          console.log({ fromDate, toDate });
        }}
          markingType={'period'}
          markedDates={
            (() => {
              if (fromDate && toDate) {
                // Mark all dates in the range
                const range: Record<string, { color: string; textColor: string; startingDay?: boolean; endingDay?: boolean; selected?: boolean; }> = {};
                const start = new Date(fromDate);
                const end = new Date(toDate);
                for (
                  let d = new Date(start);
                  d <= end;
                  d.setDate(d.getDate() + 1)
                ) {
                  const dateStr = d.toISOString().split('T')[0];
                  range[dateStr] = {
                    color: '#70d7c7',
                    textColor: 'white',
                    startingDay: dateStr === fromDate,
                    endingDay: dateStr === toDate,
                    selected: true,
                  };
                }
                return range;
              } else if (fromDate) {
                return {
                  [fromDate]: {
                    color: '#70d7c7',
                    textColor: 'white',
                    startingDay: true,
                    endingDay: true,
                    selected: true,
                  }
                };
              } else {
                return {};
              }
            })()
          }
        />

        {/* Body */}
        <View style={styles.priceContainer}>
          <View style={styles.row}>
            <Text style={styles.text}>Price Per Day:</Text>
            <Text style={styles.text}>${location.pricePerHour?.toFixed(2)}</Text>

          </View>
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
  option: {
    flex: 1,
    paddingVertical: 14,
    alignItems: "center",
    justifyContent: "center",
  },
  active: {
    backgroundColor: "#705b91ff",
  },
  label: {
    fontSize: 16,
    fontWeight: "600",
    color: "#374151",
  },
  activeLabel: {
    color: "#ffffffff",
  },
  ModeSwitchContainer: {
    flexDirection: "row",
    borderRadius: 999,
    backgroundColor: "#4d5566ff",
    alignSelf: "center",
    overflow: "hidden",
  },
  switchContainer: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 16,
  },
  priceContainer: {
    backgroundColor: "#fff",
    borderRadius: 20,
    padding: 20,
    marginVertical: 16,
    shadowColor: "#000",
    shadowOpacity: 0.08,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 6,
    elevation: 3,
  },
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
    color: "#000000ff",
    fontSize: 16,
  },
  footer: {
    flexDirection: "column",
    justifyContent: "space-between",
    padding: 16,
  },
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginVertical: 6,
  },
  button: {
    padding: 12,
    borderRadius: 8,
    alignItems: "center",
    marginHorizontal: 4,
  },
});
