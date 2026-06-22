package com.example.automata_backend.algorithms;

import com.example.automata_backend.regex.*;

public class RegexTokeniser {
    private final String regex;
    private int position;


    public RegexTokeniser(String regex){
        this.regex = regex;
    }


    public TokenisedRegex tokeniseRegex(){
        TokenisedRegex tRegex = tokeniseUnion();
        if(!hasRemaining()){
            return tRegex;
        }
        else{
            throw new IllegalArgumentException("Invalid Regex");
        }
    }

    private TokenisedRegex tokeniseUnion(){
        TokenisedRegex leftRegex = tokeniseConcat();
        while(hasRemaining() && currentChar() == '|'){
            this.consumeToken('|');
            TokenisedRegex rightRegex = tokeniseConcat();
            leftRegex = new UnionTokenRegex(leftRegex,rightRegex);
        }
        return leftRegex;
    }


    private TokenisedRegex tokeniseConcat(){
        TokenisedRegex leftRegex = tokeniseStarred();
        while(hasRemaining() && isBeginningOfInnerRegexToken(currentChar())){
            TokenisedRegex rightRegex  = tokeniseStarred();
            leftRegex = new ConcatTokenRegex(leftRegex,rightRegex);
        }

        return leftRegex;
    }

    private TokenisedRegex tokeniseCharacter(){
        if(!hasRemaining()){
            throw new IllegalArgumentException("invalid regex");
        }

        if(currentChar() == '('){
            consumeToken('(');
            TokenisedRegex node = tokeniseUnion();
            consumeToken(')');
            return node;
        }

        char symbol = currentChar();
        consumeToken(symbol);
        return new CharacterTokenRegex(symbol);

    }


    private TokenisedRegex tokeniseStarred(){
        TokenisedRegex tRegex = tokeniseCharacter();

        while(hasRemaining() && currentChar() == '*'){
            consumeToken('*');
            tRegex = new StarredTokenRegex(tRegex);
        }
        return tRegex;
    }


    private boolean isBeginningOfInnerRegexToken(char c){
        return Character.isLetterOrDigit(c) || c == '(';
    }

    private boolean hasRemaining(){
        return this.position < regex.length();
    }


    private void consumeToken(char c){
        if(!hasRemaining() || regex.charAt(this.position) != c){
            throw new IllegalArgumentException("Invalid consumption");
        }
        this.position++;
    }

    private char currentChar(){
        return regex.charAt(position);
    }


}
