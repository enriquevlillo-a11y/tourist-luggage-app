import { View, Text, TouchableOpacity, TextInput } from 'react-native'
import React from 'react'

export default function InputField({label,icon,inputType,keyboardType,fieldButtonLabel,fieldButtonFunction}) {
  return (
    <View 
           style={{
             flexDirection: 'row', 
             borderBottomColor: '#ccc', 
             borderBottomWidth:1, 
             paddingBottom:8,
             marginBottom:25,}} >
           {icon}

           {inputType == 'password' ? (
            <TextInput
             placeholder = {label}
             keyboardType = {keyboardType}
             style={{ flex:1, paddingVertical:0}}
             secureTextEntry={true}
            
           />) : (
            <TextInput
             placeholder = {label}
             keyboardType = {keyboardType}
             style={{ flex:1, paddingVertical:0}}
            />

           )}


           <TouchableOpacity onPress ={fieldButtonFunction }>
             <Text style ={{color: '#0e0c6d81', fontWeight: '700' }}>{fieldButtonLabel}</Text>
           </TouchableOpacity>
   
         </View>
  )
}