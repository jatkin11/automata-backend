package com.example.automata_backend.regex;

public class UnionTokenRegex implements TokenisedRegex{
    private final TokenisedRegex left;
    private final TokenisedRegex right;

    public UnionTokenRegex(TokenisedRegex left, TokenisedRegex right){
        this.left = left;
        this.right = right;
    }

    public TokenisedRegex getRight() {
        return right;
    }

    public TokenisedRegex getLeft() {
        return left;
    }
}
