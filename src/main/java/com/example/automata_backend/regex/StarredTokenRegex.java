package com.example.automata_backend.regex;

public class StarredTokenRegex implements TokenisedRegex {
    private final TokenisedRegex starredToken;

    public StarredTokenRegex(TokenisedRegex starredToken){
        this.starredToken = starredToken;
    }

    public TokenisedRegex getStarredToken() {
        return starredToken;
    }
}
