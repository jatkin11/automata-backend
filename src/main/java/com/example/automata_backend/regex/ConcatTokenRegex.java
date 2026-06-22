package com.example.automata_backend.regex;

public class ConcatTokenRegex implements TokenisedRegex{
    private final TokenisedRegex left;
    private final TokenisedRegex right;

    public ConcatTokenRegex(TokenisedRegex left, TokenisedRegex right){
        this.left = left;
        this.right = right;
    }

    public TokenisedRegex getLeft() {
        return left;
    }

    public TokenisedRegex getRight() {
        return right;
    }
}
