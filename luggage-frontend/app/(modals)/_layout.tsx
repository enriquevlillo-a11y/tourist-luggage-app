import { Stack } from "expo-router";
import { SafeAreaProvider } from "react-native-safe-area-context";

export default function ModalLayout() {
    return (
        <SafeAreaProvider>
            <Stack
                screenOptions={{
                    presentation: "modal",        // bottom-up on iOS
                    headerShown: false,
                }}
            />
        </SafeAreaProvider>
    );
}
