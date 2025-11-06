import React from "react";
import {
  View,
  Text,
  TouchableOpacity,
  TextInput,
  StyleSheet,
  KeyboardTypeOptions,
} from "react-native";

// ✅ Define the expected props and mark optional ones with `?`
interface InputFieldProps {
  label: string;
  icon?: React.ReactNode;
  inputType?: string;
  keyboardType?: KeyboardTypeOptions;
  fieldButtonLabel?: string;
  fieldButtonFunction?: () => void;
}

const InputField: React.FC<InputFieldProps> = ({
  label,
  icon,
  inputType,
  keyboardType,
  fieldButtonLabel,
  fieldButtonFunction,
}) => {
  return (
    <View style={styles.container}>
      {icon}

      <TextInput
        placeholder={label}
        keyboardType={keyboardType}
        style={styles.input}
        secureTextEntry={inputType === "password"}
      />

      {fieldButtonLabel && (
        <TouchableOpacity onPress={fieldButtonFunction}>
          <Text style={styles.buttonText}>{fieldButtonLabel}</Text>
        </TouchableOpacity>
      )}
    </View>
  );
};

export default InputField;

// ✅ Styles moved out of inline code
const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    borderBottomColor: "#ccc",
    borderBottomWidth: 1,
    paddingBottom: 8,
    marginBottom: 25,
  },
  input: {
    flex: 1,
    paddingVertical: 0,
  },
  buttonText: {
    color: "#0e0c6d81",
    fontWeight: "700",
  },
});