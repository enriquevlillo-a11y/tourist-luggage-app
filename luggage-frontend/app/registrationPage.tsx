import React, { useState } from "react";
import { View, Text, Image, TouchableOpacity, ScrollView, KeyboardAvoidingView, Platform } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import InputField from "../components/InputField";
import DateTimePicker from '@react-native-community/datetimepicker';
import GoogleIcon from "../assets/google.svg";
import FacebookIcon from "../assets/facebook.svg";
import RegistrationImg from "../assets/MapIcon.png";
import MaterialIcons from "react-native-vector-icons/MaterialIcons";
import Ionicons from "react-native-vector-icons/Ionicons"; 

export default function RegisterScreen() {
  const router = useRouter();
  const [date, setDate] = useState(new Date());
  const [showPicker, setShowPicker] = useState(false); 
  const [dobLabel, setDobLabel] = useState("Date of Birth");

  const toggleDatePicker = () => setShowPicker(!showPicker);

  const onChange = ({ type }, selectedDate) => {
    if (type === "set") {
      const currentDate = selectedDate;
      const formattedDate = currentDate.toLocaleDateString("en-US", {
        month: "short",
        day: "numeric",
        year: "numeric",
      });
      setDate(currentDate);
      setDobLabel(formattedDate);
      setShowPicker(false);
    } else {
      setShowPicker(false);
    }
  };

  return (
    <SafeAreaView style={{ flex: 1 }}>
      {/* This makes the view shift up when keyboard appears */}
      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === "ios" ? "padding" : "height"}
      >
        <ScrollView
          showsVerticalScrollIndicator={false}
          style={{ paddingHorizontal: 25 }}
          contentContainerStyle={{ flexGrow: 1 }}
          keyboardShouldPersistTaps="handled"
        >
          <View style={{ alignItems: "center" }}>
            <Image 
              source={RegistrationImg} 
              style={{ width: 200, height: 200, marginBottom: 30 }}
              resizeMode="contain"
            />
          </View>

          <Text style={{ fontSize: 22, fontWeight: "500", color: "#333", marginBottom: 30 }}>
            Register
          </Text>

          <View style={{
            flexDirection: "row",
            justifyContent: "space-around",
            marginBottom: 30,
          }}>
            <TouchableOpacity
              onPress={() => {}}
              style={{
                borderColor: "#ddd",
                borderWidth: 2,
                borderRadius: 10,
                paddingHorizontal: 30,
                paddingVertical: 10,
              }}
            >
              <GoogleIcon height={24} width={24} />
            </TouchableOpacity>

            <TouchableOpacity
              onPress={() => {}}
              style={{
                borderColor: "#ddd",
                borderWidth: 2,
                borderRadius: 10,
                paddingHorizontal: 30,
                paddingVertical: 10,
              }}
            >
              <FacebookIcon height={24} width={24} />
            </TouchableOpacity>
          </View>

          {/* Registering with email */}
          <Text style={{ textAlign: "center", color: "#666", marginBottom: 30 }}>
            Or Register with email ...
          </Text>

          <InputField
            label={"Full name"}
            icon={
              <Ionicons
                name="person-outline"
                size={20}
                color="#666"
                style={{ marginRight: 5 }}
              />
            }
          />

          <View
            style={{
              flexDirection: "row",
              borderBottomColor: "#ccc",
              borderBottomWidth: 1,
              paddingBottom: 8,
              marginBottom: 30,
            }}
          >
            <Ionicons
              name="calendar-outline"
              size={20}
              color="#666"
              style={{ marginRight: 5 }}
            />
            <TouchableOpacity onPress={() => setShowPicker(true)}>
              <Text style={{ color: "#666", marginLeft: 5, marginTop: 5 }}>
                {dobLabel}
              </Text>
            </TouchableOpacity>
          </View>

          {showPicker && (
            <DateTimePicker
              mode="date"
              display="spinner"
              value={date}
              onChange={onChange}
              minimumDate={new Date(1940, 0, 1)}
              maximumDate={new Date()}
            />
          )}

          <InputField
            label={"Email ID"}
            icon={
              <MaterialIcons
                name="alternate-email"
                size={20}
                color="#666"
                style={{ marginRight: 5 }}
              />
            }
            keyboardType="email-address"
          />

          <InputField
            label={"Password"}
            icon={
              <Ionicons
                name="lock-closed-outline"
                size={20}
                color="#666"
                style={{ marginRight: 5 }}
              />
            }
            inputType="password"
          />

          <InputField
            label={"Confirm Password"}
            icon={
              <Ionicons
                name="lock-closed-outline"
                size={20}
                color="#666"
                style={{ marginRight: 5 }}
              />
            }
            inputType="password"
          />

          <TouchableOpacity
            onPress={() => {}}
            style={{
              backgroundColor: "#0e0c6d99",
              padding: 20,
              borderRadius: 10,
              marginBottom: 30,
            }}
          >
            <Text
              style={{
                textAlign: "center",
                fontWeight: "700",
                fontSize: 16,
                color: "#fff",
              }}
            >
              Register
            </Text>
          </TouchableOpacity>

          <View style={{ flexDirection: "row", justifyContent: "center", marginBottom: 30 }}>
            <Text>Already a member? </Text>
            <TouchableOpacity onPress={() => router.back()}>
              <Text style={{ color: "#0e0c6d99", fontWeight: "700" }}> Login Here </Text>
            </TouchableOpacity>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}