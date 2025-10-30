//This is where we define our data model for the front end. 
//We also define our Zustand store for managing spots data globally.
//This includes functions to set spots, get a spot by its ID, and add reviews to a spot.

import {create} from "zustand";
import { MOCK_SPOTS } from "../data/mockSpots";

export type Review = {
    id: string;
    user: string;
    comment: string;
    rating: number; //1..5
    createdAt: string //Iso
}

export type Location = {
    id: string; //Changed to a string to match backend UUIDs 
    name: string; 
    pricePerHour: number; 
    address: string; 
    rating: number, 
    latitude: number, 
    longitude: number, 
    reviews: Review[]};

type State = {
    locations: Location[];
    setSpots: (locations: Location[]) => void;
    getById: (id: string) => Location | undefined;
    addReview: (locationId: string, review: Review) => void;

};


function averageStars(reviews: Review[]): number {
  if (!reviews.length) return 0;
  const sum = reviews.reduce((acc, r) => acc + r.rating, 0);
  // round to 1 decimal like 4.3
  return Math.round((sum / reviews.length) * 10) / 10;
}

export const useSpotsStore = create<State>((set, get) => ({
  // In dev mode, initialize with mock data, otherwise start empty
    locations: (typeof __DEV__ !== "undefined" && __DEV__) || process.env.NODE_ENV === "development" ? MOCK_SPOTS : [],
    setSpots: (locations) => set({ locations }),
    getById: (id) => get().locations.find((s) => s.id === id),
    addReview: (locationId, review) =>
                  set((state) => {
            const spots = state.locations.map((s) => {
              if (s.id !== locationId) return s;
              const reviews = [...s.reviews, review];
              return { ...s, reviews, rating: averageStars(reviews) };
            });
            return { locations: spots };
          }),
}));