//This is where we define our data model for the front end. 

import {create} from "zustand";
import { MOCK_SPOTS } from "../app/data/mockSpots";

export type Review = {
    id: string;
    user: string;
    comment: string;
    rating: number; //1..5
    createdAt: string //Iso
}

export type Spot = {
    id: number; 
    name: string; 
    price: number; 
    address: string; 
    rating: number, 
    lat: number, 
    long: number, 
    reviews: Review[]};

type State = {
    spots: Spot[];
    setSpots: (spots: Spot[]) => void;
    getById: (id: number) => Spot | undefined;
    addReview: (spotId: number, review: Review) => void;
    
};


function averageStars(reviews: Review[]): number {
  if (!reviews.length) return 0;
  const sum = reviews.reduce((acc, r) => acc + r.rating, 0);
  // round to 1 decimal like 4.3
  return Math.round((sum / reviews.length) * 10) / 10;
}

export const useSpotsStore = create<State>((set, get) => ({
    spots: MOCK_SPOTS,
    setSpots: (spots) => set({spots}),
    getById: (id) => get().spots.find((s) => s.id === id),
    addReview: (spotId, review) =>
                  set((state) => {
            const spots = state.spots.map((s) => {
              if (s.id !== spotId) return s;
              const reviews = [...s.reviews, review];
              return { ...s, reviews, rating: averageStars(reviews) };
            });
            return { spots };
          }),
}));