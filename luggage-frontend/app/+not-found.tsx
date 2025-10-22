import React, { JSX } from "react";
import { useNavigation, CommonActions } from "@react-navigation/native";
import { SafeAreaProvider } from "react-native-safe-area-context";

import {
View,
Text,
StyleSheet,
Pressable,
GestureResponderEvent,
} from "react-native";

type Nav = {
goBack: () => void;
dispatch: (action: any) => void;
};

export default function NotFoundScreen(): JSX.Element {
const navigation = useNavigation<Nav>();

const handleGoBack = (e?: GestureResponderEvent) => {
    if (navigation?.goBack) navigation.goBack();
};

const handleGoHome = (e?: GestureResponderEvent) => {
    if (navigation?.dispatch) {
        navigation.dispatch(
            CommonActions.reset({
                index: 0,
                routes: [{ name: "Home" }],
            })
        );
    }
};

return (
    <SafeAreaProvider style={styles.safe}>
        <View style={styles.container}>
            <Text style={styles.code}>404</Text>
            <Text style={styles.title}>Page not found</Text>
            <Text style={styles.subtitle}>
                The screen you are looking for doesn't exist or has been moved.
            </Text>

            <View style={styles.buttons}>
                <Pressable
                    onPress={handleGoBack}
                    style={({ pressed }) => [styles.button, pressed && styles.pressed]}
                    accessibilityLabel="Go back"
                    testID="notfound-go-back"
                >
                    <Text style={styles.buttonText}>Go back</Text>
                </Pressable>

                <Pressable
                    onPress={handleGoHome}
                    style={({ pressed }) => [styles.button, styles.primary, pressed && styles.pressed]}
                    accessibilityLabel="Go home"
                    testID="notfound-go-home"
                >
                    <Text style={[styles.buttonText, styles.primaryText]}>Go home</Text>
                </Pressable>
            </View>
        </View>
    </SafeAreaProvider>
);
}

const styles = StyleSheet.create({
safe: {
    flex: 1,
    backgroundColor: "#fff",
},
container: {
    flex: 1,
    paddingHorizontal: 24,
    alignItems: "center",
    justifyContent: "center",
},
code: {
    fontSize: 72,
    fontWeight: "700",
    color: "#111827",
    marginBottom: 8,
},
title: {
    fontSize: 22,
    fontWeight: "600",
    color: "#111827",
    marginBottom: 6,
},
subtitle: {
    fontSize: 14,
    color: "#6b7280",
    textAlign: "center",
    marginBottom: 24,
    maxWidth: 320,
},
buttons: {
    flexDirection: "row",
    gap: 12,
},
button: {
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderRadius: 8,
    backgroundColor: "#f3f4f6",
    marginHorizontal: 6,
},
primary: {
    backgroundColor: "#111827",
},
pressed: {
    opacity: 0.8,
},
buttonText: {
    color: "#111827",
    fontWeight: "600",
},
primaryText: {
    color: "#fff",
},
});