package com.example.automata_backend.algorithms;

public class GlobalStateIDGenerator {

    private  int nextId = 0;

    public int next(){
        return nextId++;
    }

}
