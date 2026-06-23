package com.example.automata_backend.algorithms;

import com.example.automata_backend.regex.CharacterTokenRegex;
import com.example.automata_backend.regex.ConcatTokenRegex;
import com.example.automata_backend.regex.EmptyTokenRegex;
import com.example.automata_backend.regex.StarredTokenRegex;
import com.example.automata_backend.regex.TokenisedRegex;
import com.example.automata_backend.regex.UnionTokenRegex;

public class TokenisedRegexToString {

    public static String toRegexString(TokenisedRegex regex) {
        if (regex instanceof CharacterTokenRegex character) {
            return String.valueOf(character.getSingleToken());
        }

        if (regex instanceof EmptyTokenRegex) {
            return "ε";
        }

        if (regex instanceof StarredTokenRegex starred) {
            return "(" + toRegexString(starred.getStarredToken()) + ")*";
        }

        if (regex instanceof ConcatTokenRegex concat) {
            return "(" + toRegexString(concat.getLeft()) 
                    + toRegexString(concat.getRight()) + ")";
        }

        if (regex instanceof UnionTokenRegex union) {
            return "(" + toRegexString(union.getLeft()) 
                    + "|" 
                    + toRegexString(union.getRight()) + ")";
        }

        throw new IllegalArgumentException("Unknown regex token type: " + regex.getClass());
    }
}