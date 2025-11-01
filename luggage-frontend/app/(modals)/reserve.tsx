import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { useRouter } from "expo-router";
import {
  SafeAreaView,
  SafeAreaProvider,
} from 'react-native-safe-area-context';

import { Calendar, CalendarList, Agenda, ExpandableCalendar } from "react-native-calendars";
import { useState } from "react";

export default function TemplateModal() {
  const router = useRouter();
  
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');


  return (
    <SafeAreaProvider>
      <SafeAreaView style={styles.container}>

        {/* Header */}
        <Calendar onDayPress={day => {
          if(!fromDate || (fromDate && toDate)) {
            setFromDate(day.dateString);
            setToDate('');
          }else if (fromDate && !toDate) {
            if (day.dateString > fromDate) {
              setToDate(day.dateString);
            } else {
              // If selected date is before or same as fromDate, start new selection
              setFromDate(day.dateString);
              setToDate('');
            }
          }
          console.log('selected day', day);
          console.log({fromDate, toDate});
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
