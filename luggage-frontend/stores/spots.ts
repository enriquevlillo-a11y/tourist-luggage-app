//This is where we define our data model for the front end. 

import {create} from "zustand";

export type Spot = {id: number; name: string; price: number; address: string; rating: number};

type State = {
    spots: Spot[];
    setSpots: (spots: Spot[]) => void;
    getById: (id: number) => Spot | undefined;
};

export const useSpotsStore = create<State>((set, get) => ({
    spots: [],
    setSpots: (spots) => set({spots}),
    getById: (id) => get().spots.find((s) => s.id === id),
}));