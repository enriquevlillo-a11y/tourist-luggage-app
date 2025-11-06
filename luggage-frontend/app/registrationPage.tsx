import React, { useState } from "react";
import {
  View,
  Text,
  Image,
  TouchableOpacity,
  ScrollView,
  KeyboardAvoidingView,
  Platform,
  StyleSheet,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import InputField from "../components/InputField";
import DateTimePicker from "@react-native-community/datetimepicker";
import GoogleIcon from "../assets/google.svg";
import FacebookIcon from "../assets/facebook.svg";
import RegistrationImg from "../assets/MapIcon.png";
import { Ionicons, MaterialIcons } from "@expo/vector-icons";

export default function RegisterScreen() {
  const router = useRouter();
  const [date, setDate] = useState(new Date());
  const [showPicker, setShowPicker] = useState(false);
  const [dobLabel, setDobLabel] = useState("Date of Birth");

  const toggleDatePicker = () => setShowPicker(!showPicker);

  const onChange = ({ type }: any, selectedDate: Date | undefined) => {
    if (type === "set" && selectedDate) {
      const formattedDate = selectedDate.toLocaleDateString("en-US", {
        month: "short",
        day: "numeric",
        year: "numeric",
      });
      setDate(selectedDate);
      setDobLabel(formattedDate);
    }
    setShowPicker(false);
  };

  return (
    <SafeAreaView style={styles.container}>
      <KeyboardAvoidingView
        style={styles.container}
        behavior={Platform.OS === "ios" ? "padding" : "height"}
      >
        <ScrollView
          showsVerticalScrollIndicator={false}
          style={styles.scrollView}
          contentContainerStyle={styles.scrollContent}
          keyboardShouldPersistTaps="handled"
        >
          <View style={styles.imageContainer}>
            <Image
              source={RegistrationImg}
              style={styles.image}
              resizeMode="contain"
            />
          </View>

          <Text style={styles.header}>Register</Text>

          <View style={styles.socialContainer}>
            <TouchableOpacity onPress={() => {}} style={styles.socialButton}>
              <GoogleIcon height={24} width={24} />
            </TouchableOpacity>

            <TouchableOpacity onPress={() => {}} style={styles.socialButton}>
              <FacebookIcon height={24} width={24} />
            </TouchableOpacity>
          </View>

          <Text style={styles.orText}>Or Register with email ...</Text>

          <InputField
            label="Full name"
            icon={
              <Ionicons
                name="person-outline"
                size={20}
                color="#666"
                style={styles.icon}
              />
            }
          />

          <View style={styles.dateContainer}>
            <Ionicons
              name="calendar-outline"
              size={20}
              color="#666"
              style={styles.icon}
            />
            <TouchableOpacity onPress={toggleDatePicker}>
              <Text style={styles.dateText}>{dobLabel}</Text>
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
            label="Email ID"
            icon={
              <MaterialIcons
                name="alternate-email"
                size={20}
                color="#666"
                style={styles.icon}
              />
            }
            keyboardType="email-address"
          />

          <InputField
            label="Password"
            icon={
              <Ionicons
                name="lock-closed-outline"
                size={20}
                color="#666"
                style={styles.icon}
              />
            }
            inputType="password"
          />

          <InputField
            label="Confirm Password"
            icon={
              <Ionicons
                name="lock-closed-outline"
                size={20}
                color="#666"
                style={styles.icon}
              />
            }
            inputType="password"
          />

          <TouchableOpacity onPress={() => {}} style={styles.registerButton}>
            <Text style={styles.registerButtonText}>Register</Text>
          </TouchableOpacity>

          <View style={styles.footer}>
            <Text>Already a member?</Text>
            <TouchableOpacity onPress={() => router.back()}>
              <Text style={styles.footerLink}> Login Here </Text>
            </TouchableOpacity>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scrollView: {
    paddingHorizontal: 25,
  },
  scrollContent: {
    flexGrow: 1,
  },
  imageContainer: {
    alignItems: "center",
  },
  image: {
    width: 200,
    height: 200,
    marginBottom: 30,
  },
  header: {
    fontSize: 22,
    fontWeight: "500",
    color: "#333",
    marginBottom: 30,
  },
  socialContainer: {
    flexDirection: "row",
    justifyContent: "space-around",
    marginBottom: 30,
  },
  socialButton: {
    borderColor: "#ddd",
    borderWidth: 2,
    borderRadius: 10,
    paddingHorizontal: 30,
    paddingVertical: 10,
  },
  orText: {
    textAlign: "center",
    color: "#666",
    marginBottom: 30,
  },
  icon: {
    marginRight: 5,
  },
  dateContainer: {
    flexDirection: "row",
    borderBottomColor: "#ccc",
    borderBottomWidth: 1,
    paddingBottom: 8,
    marginBottom: 30,
  },
  dateText: {
    color: "#666",
    marginLeft: 5,
    marginTop: 5,
  },
  registerButton: {
    backgroundColor: "#0e0c6d99",
    padding: 20,
    borderRadius: 10,
    marginBottom: 30,
  },
  registerButtonText: {
    textAlign: "center",
    fontWeight: "700",
    fontSize: 16,
    color: "#fff",
  },
  footer: {
    flexDirection: "row",
    justifyContent: "center",
    marginBottom: 30,
  },
  footerLink: {
    color: "#0e0c6d99",
    fontWeight: "700",
  },
});