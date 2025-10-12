import { Stack } from "expo-router";

export default function RootLayout() {
  return (
    <Stack>
      <Stack.Screen
      name="index"
      options={{
        title: "Luggo",
        headerStyle: { backgroundColor: "#1e90ff"},
        headerTintColor: "#fff",
        headerTitleAlign: "left",
      }}
      />
    </Stack>
  )
}
