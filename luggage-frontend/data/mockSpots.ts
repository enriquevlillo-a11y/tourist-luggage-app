import { Location } from "../stores/spots";

export const MOCK_SPOTS: Location[] = [
  {
    id: "1",
    name: "Locker Center",
    pricePerHour: 6,
    address: "Brickell Ave",
    rating: 4.5,
    latitude: 25.7617, longitude: -80.1918,
    reviews: [
      { id: "r1", user: "Alice", comment: "Clean and safe lockers.", rating: 5, createdAt: "2025-10-15T10:12:00Z" },
      { id: "r2", user: "Mark",  comment: "Easy access, great location.", rating: 4, createdAt: "2025-10-14T15:22:00Z" },
    ],
  },
  {
    id: "2",
    name: "Hotel Plaza",
    pricePerHour: 8,
    address: "Downtown",
    rating: 4.2,
    latitude: 25.7736, longitude: -80.1937,
    reviews: [{ id: "r3", user: "Laura", comment: "Nice service!", rating: 4, createdAt: "2025-09-30T18:01:00Z" }],
  },
  { 
    id: "3", 
    name: "Cafe Storage", 
    pricePerHour: 5, 
    address: "Wynwood", 
    rating: 4.0, 
    latitude: 25.8007, 
    longitude: -80.1994, 
    reviews: [] 
},
  { 
    id: "4", 
    name: "Bayfront Lockers", 
    pricePerHour: 7, 
    address: "Biscayne Blvd", 
    rating: 4.3, 
    latitude: 25.7825, 
    longitude: -80.1856, 
    reviews: [] 
},
  { id: "5", 
    name: "Airport Storage Hub", 
    pricePerHour: 10, 
    address: "Miami International Airport", 
    rating: 4.7, 
    latitude: 25.7959, 
    longitude: -80.2870, 
    reviews: [] 
},
];
