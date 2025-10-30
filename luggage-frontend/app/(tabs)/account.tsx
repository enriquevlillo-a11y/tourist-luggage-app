import { View, Text, Image, TextInput,TouchableOpacity } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import googleIcon from "../../assets/google.svg"
import facebookIcon from "../../assets/facebook.svg"
import LoginImg from "../../assets/AccountLogo.png";
import MaterialIcons from "react-native-vector-icons/MaterialIcons"; //Default icons from react to get @
import Ionicons from "react-native-vector-icons/Ionicons"; 
export default function Account() {
  return (
    <SafeAreaView style={{flex:1, justifyContent: 'center'}}> 
    <View style={{ paddingHorizontal:25 }}>
    <View style={{alignItems:"center"}}>

      <Image 
        source={LoginImg} 
        style={{ width: 200, height: 200, marginBottom: 100 }}
        resizeMode="contain"
      />
      </View>

      <Text style={{ fontSize:22, fontWeight:"500", color: '#333',marginBottom: 30, }}>Account</Text>

      <View 
        style={{
          flexDirection: 'row', 
          borderBottomColor: '#ccc', 
          borderBottomWidth:1, 
          paddingBottom:8,
          marginBottom:25,}} >
        <MaterialIcons
          name='alternate-email'
          size={20}
          color= "#666"
          style = {{marginRight:5}}
         />
        <TextInput
          placeholder = 'Email ID' 
          style={{ flex:1, paddingVertical:0}}
          //PaddingVertical for Android Users, so the background color doesnt combine with the @
          keyboardType="email-address"
        /> 
      </View>

       <View 
        style={{
          flexDirection: 'row', 
          borderBottomColor: '#ccc', 
          borderBottomWidth:1, 
          paddingBottom:8,
          marginBottom:25,}} >
        <Ionicons
          name= "lock-closed-outline"
          size={20}
          color= "#666"
          style = {{marginRight:5}}
         />
        <TextInput
          placeholder = "Password"
          style={{ flex:1, paddingVertical:0}}
          secureTextEntry={true}
         
        /> 
        <TouchableOpacity onPress ={ () => {} }>
          <Text style ={{color: '#0e0c6d81', fontWeight: '700' }}>Forgot Password</Text>
        </TouchableOpacity>

      </View>
      <TouchableOpacity

      onPress ={ () => {} } 
      style={{
      backgroundColor: '#0e0c6d99', 
      padding: 20, 
      borderRadius: 10, 
      marginBottom: 30,
      }}>
        <Text style={{textAlign:'center',fontWeight: '700', fontSize:16, color: '#fff'}}>Login</Text>
      </TouchableOpacity>
      
      //Logging in with social media accounts.
      <Text style= {{textAlign: 'center', color: '#666', marginBottom: 30 }}>
        Or login with...
        </Text>
        

    </View>
    </SafeAreaView>
  );
}