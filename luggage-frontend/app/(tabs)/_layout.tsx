  import { Tabs } from "expo-router";
import { Image } from "react-native";

export default function TabsLayout() {
  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarShowLabel: true,
        tabBarActiveTintColor: "#007AFF", // iOS blue, can change
        tabBarInactiveTintColor: "#999",
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: "Home",
          tabBarIcon: ({ focused }) => (
            <Image
              source={require("../../assets/homeIcon.png")}
              style={{
                width: 24,
                height: 24,
                tintColor: focused ? "#007AFF" : "#999",
              }}
              resizeMode="contain"
            />
          ),
        }}
      />

      <Tabs.Screen
        name="map"
        options={{
          title: "Map",
          tabBarIcon: ({ focused }) => (
            <Image
              source={require("../../assets/MapIcon.png")}
              style={{
                width: 24,
                height: 24,
                
              }}
              resizeMode="contain"
              
            />
          ),
        }}
      />

      <Tabs.Screen
        name="account"
        options={{
          title: "Account",
          tabBarIcon: ({ focused }) => (
            <Image
              source={require("../../assets/accountIcon.png")}
              style={{
                width: 24,
                height: 24,
                tintColor: focused ? "#007AFF" : "#999",
              }}
              resizeMode="contain"
            />
          ),
        }}
      />
    </Tabs>
  );
}