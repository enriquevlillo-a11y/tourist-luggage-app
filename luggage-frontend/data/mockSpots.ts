import { Spot } from "../stores/spots";

export const MOCK_SPOTS: Spot[] = [
  {
    id: 1,
    name: "Locker Center",
    price: 6,
    address: "Brickell Ave",
    rating: 4.5,
    lat: 25.7617, long: -80.1918,
    reviews: [
      { id: "r1", user: "Alice", comment: "Clean and safe lockers.", rating: 5, createdAt: "2025-10-15T10:12:00Z" },
      { id: "r2", user: "Mark",  comment: "Easy access, great location.", rating: 4, createdAt: "2025-10-14T15:22:00Z" },
    ],
  },
  {
    id: 2,
    name: "Hotel Plaza",
    price: 8,
    address: "Downtown",
    rating: 4.2,
    lat: 25.7736, long: -80.1937,
    reviews: [{ id: "r3", user: "Laura", comment: "Nice service!", rating: 4, createdAt: "2025-09-30T18:01:00Z" }],
  },
  { 
    id: 3, 
    name: "Cafe Storage", 
    price: 5, 
    address: "Wynwood", 
    rating: 4.0, 
    lat: 25.8007, 
    long: -80.1994, 
    reviews: [] 
},
  { 
    id: 4, 
    name: "Bayfront Lockers", 
    price: 7, 
    address: "Biscayne Blvd", 
    rating: 4.3, 
    lat: 25.7825, 
    long: -80.1856, 
    reviews: [] 
},
  { id: 5, 
    name: "Airport Storage Hub", 
    price: 10, 
    address: "Miami International Airport", 
    rating: 4.7, 
    lat: 25.7959, 
    long: -80.2870, 
    reviews: [] 
},
];
