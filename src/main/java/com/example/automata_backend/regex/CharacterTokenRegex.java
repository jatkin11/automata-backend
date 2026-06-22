package com.example.automata_backend.regex;

public class CharacterTokenRegex implements TokenisedRegex{
    private final char singleToken;
    
    public CharacterTokenRegex(char c){
        this.singleToken = c;
    }

    public char getSingleToken() {
        return singleToken;
    }
}
