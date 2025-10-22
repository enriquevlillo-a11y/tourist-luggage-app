//This sets up our stack navigation for the rest of the app. 

import { Stack } from "expo-router";
import { SafeAreaProvider } from "react-native-safe-area-context";
import { Button } from "react-native";

function AccountButton() {
  return (
    <Button title="Account" />
  )
}

export default function RootLayout() {
  return (
    <SafeAreaProvider>
      <Stack
        screenOptions={{
          title: 'Luggo',
          headerShown: true,
          headerRight: AccountButton,
          headerStyle: {
            backgroundColor: '#a7bbf6'
          },
          headerTitleStyle: {
            fontWeight: 'bold'
          }
        }} >
        <Stack.Screen
          name="(tabs)"
        />
        <Stack.Screen name = "spot" />

      </Stack>
    </SafeAreaProvider>
  );
}
