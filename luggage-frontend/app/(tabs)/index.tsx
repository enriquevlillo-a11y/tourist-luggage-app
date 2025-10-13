import { useState } from "react";
import { View, Text, FlatList, TextInput, TouchableOpacity } from "react-native";
import { Link } from "expo-router";

const MOCK = [
  { id:1, name:"Locker Center", price:6, address:"Brickell Ave" },
  { id:2, name:"Hotel Plaza",   price:8, address:"Downtown" },
  { id:3, name:"Cafe Storage",  price:5, address:"Wynwood" },
];

export default function Home() {
  const [q, setQ] = useState("");
  const data = MOCK.filter(s => s.name.toLowerCase().includes(q.toLowerCase()));

  return (
    <View style={{ flex:1, paddingTop:24 }}>
      {/* Header */}
      <View style={{ paddingHorizontal:16, paddingBottom:8, flexDirection:"row", justifyContent:"space-between", alignItems:"center" }}>
        <Text style={{ fontSize:28, fontWeight:"800" }}>App Name</Text>
        <View style={{ width:34, height:34, borderRadius:17, borderWidth:2 }} />
      </View>

      {/* Search */}
      <View style={{ paddingHorizontal:16, marginVertical:12 }}>
        <TextInput
          placeholder="Search"
          value={q}
          onChangeText={setQ}
          style={{ backgroundColor:"#F4F4F5", padding:12, borderRadius:12, borderWidth:1, borderColor:"#E5E7EB", fontSize:16 }}
        />
      </View>

      {/* “Mapa” placeholder */}
      <View style={{ margin:16, height:120, backgroundColor:"#EEF2FF", borderRadius:60, justifyContent:"center", alignItems:"center", borderWidth:1, borderColor:"#E5E7EB" }}>
        <Text>Map placeholder</Text>
      </View>

      {/* Lista de cards */}
      <FlatList
        contentContainerStyle={{ paddingHorizontal:16, paddingBottom:20 }}
        data={data}
        keyExtractor={(it)=>String(it.id)}
        renderItem={({item}) => (
          <Link href={`/spot/${item.id}`} asChild>
            <TouchableOpacity style={{ backgroundColor:"#fff", borderRadius:16, padding:16, marginVertical:8, elevation:2 }}>
              <Text style={{ fontWeight:"700", fontSize:16 }}>{item.name}</Text>
              <Text style={{ color:"#6B7280", marginTop:4 }}>${item.price}/h</Text>
              <Text style={{ color:"#9CA3AF", marginTop:4 }}>{item.address}</Text>
            </TouchableOpacity>
          </Link>
        )}
      />
    </View>
  );
}