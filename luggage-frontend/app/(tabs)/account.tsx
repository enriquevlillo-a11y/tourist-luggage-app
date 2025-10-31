import { View, Text, Image, TextInput,TouchableOpacity, ScrollView} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router"; {/*Navigate through screens*/}
import GoogleIcon from "../../assets/google.svg"
import FacebookIcon from "../../assets/facebook.svg"
import LuggoPNG from "../../assets/Luggo.png";
import MaterialIcons from "react-native-vector-icons/MaterialIcons"; {/*Default icons from react to get @*/}
import Ionicons from "react-native-vector-icons/Ionicons"; 


export default function Account() {
  const router = useRouter();
  return (
    <SafeAreaView style={{flex:1, justifyContent: 'center'}}> 
    <View style={{ paddingHorizontal:25 }}>
    <View style={{alignItems:"center"}}>

      <Image 
        source={LuggoPNG} 
        style={{ width: 200, height: 200, marginBottom: 30 }}
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
         {/*PaddingVertical for Android Users, so the background color doesnt combine with the @*/}
        <TextInput
          placeholder = 'Email ID' 
          style={{ flex:1, paddingVertical:0}}
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
      
      {/*Logging in with Social Media Accounts*/}
      <Text style= {{textAlign: 'center', color: '#666', marginBottom: 30 }}>
        Or login with...
        </Text>

        <View style={{flexDirection:'row', justifyContent: 'space-around', marginBottom: 30,}}>
        
        <TouchableOpacity 
        onPress = {() => {}}
        style ={{borderColor: '#ddd',
        borderWidth: 2, 
        borderRadius: 10,
        paddingHorizontal: 30,
        paddingVertical: 10,}}>

          <GoogleIcon height= {24} width= {24} />

        </TouchableOpacity>
          <TouchableOpacity 
        onPress = {() => {}}
        style ={{borderColor: '#ddd',
        borderWidth: 2, 
        borderRadius: 10,
        paddingHorizontal: 30,
        paddingVertical: 10,}}>

          <FacebookIcon height= {24} width= {24} />

        </TouchableOpacity>
        </View>

        <View style={{flexDirection: 'row', justifyContent: 'center', marginBottom: 30}}>
        <Text> New Member? </Text>
        
        <TouchableOpacity onPress = {() => router.push('/registrationPage')}>
           <Text style={{color: '#0e0c6d99', fontWeight: '700',}}> Register Here </Text>

        </TouchableOpacity>
       </View>

    </View>
    </SafeAreaView>
  );
};