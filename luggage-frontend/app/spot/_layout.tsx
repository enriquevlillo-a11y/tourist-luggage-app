// app/spot/_layout.tsx
import { Stack } from "expo-router";

export default function SpotLayout() {
  return (
    <Stack>
      <Stack.Screen name={"[id]"} options={{ headerShown: false }} />
    </Stack>
  );
}
