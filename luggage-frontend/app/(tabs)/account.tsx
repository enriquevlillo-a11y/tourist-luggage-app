import React from "react";
import {
  View,
  Text,
  Image,
  TextInput,
  TouchableOpacity,
  StyleSheet,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import GoogleIcon from "../../assets/google.svg";
import FacebookIcon from "../../assets/facebook.svg";
import LuggoPNG from "../../assets/Luggo.png";
import { Ionicons, MaterialIcons } from "@expo/vector-icons";

export default function Account() {
  const router = useRouter();

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.innerContainer}>
        <View style={styles.imageContainer}>
          <Image source={LuggoPNG} style={styles.logo} resizeMode="contain" />
        </View>

        <Text style={styles.title}>Account</Text>

        {/* Email Input */}
        <View style={styles.inputContainer}>
          <MaterialIcons
            name="alternate-email"
            size={20}
            color="#666"
            style={styles.icon}
          />
          <TextInput
            placeholder="Email ID"
            style={styles.textInput}
            keyboardType="email-address"
          />
        </View>

        {/* Password Input */}
        <View style={styles.inputContainer}>
          <Ionicons
            name="lock-closed-outline"
            size={20}
            color="#666"
            style={styles.icon}
          />
          <TextInput
            placeholder="Password"
            style={styles.textInput}
            secureTextEntry
          />
          <TouchableOpacity onPress={() => {}}>
            <Text style={styles.forgotPassword}>Forgot Password</Text>
          </TouchableOpacity>
        </View>

        {/* Login Button */}
        <TouchableOpacity onPress={() => {}} style={styles.loginButton}>
          <Text style={styles.loginText}>Login</Text>
        </TouchableOpacity>

        {/* Social Login */}
        <Text style={styles.orText}>Or login with...</Text>

        <View style={styles.socialContainer}>
          <TouchableOpacity style={styles.socialButton} onPress={() => {}}>
            <GoogleIcon height={24} width={24} />
          </TouchableOpacity>
          <TouchableOpacity style={styles.socialButton} onPress={() => {}}>
            <FacebookIcon height={24} width={24} />
          </TouchableOpacity>
        </View>

        {/* Registration Link */}
        <View style={styles.registerContainer}>
          <Text>New Member?</Text>
          <TouchableOpacity onPress={() => router.push("/registrationPage")}>
            <Text style={styles.registerLink}> Register Here </Text>
          </TouchableOpacity>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
  },
  innerContainer: {
    paddingHorizontal: 25,
  },
  imageContainer: {
    alignItems: "center",
  },
  logo: {
    width: 200,
    height: 200,
    marginBottom: 30,
  },
  title: {
    fontSize: 22,
    fontWeight: "500",
    color: "#333",
    marginBottom: 30,
  },
  inputContainer: {
    flexDirection: "row",
    borderBottomColor: "#ccc",
    borderBottomWidth: 1,
    paddingBottom: 8,
    marginBottom: 25,
    alignItems: "center",
  },
  icon: {
    marginRight: 5,
  },
  textInput: {
    flex: 1,
    paddingVertical: 0,
  },
  forgotPassword: {
    color: "#0e0c6d81",
    fontWeight: "700",
  },
  loginButton: {
    backgroundColor: "#0e0c6d99",
    padding: 20,
    borderRadius: 10,
    marginBottom: 30,
  },
  loginText: {
    textAlign: "center",
    fontWeight: "700",
    fontSize: 16,
    color: "#fff",
  },
  orText: {
    textAlign: "center",
    color: "#666",
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
  registerContainer: {
    flexDirection: "row",
    justifyContent: "center",
    marginBottom: 30,
  },
  registerLink: {
    color: "#0e0c6d99",
    fontWeight: "700",
  },
});