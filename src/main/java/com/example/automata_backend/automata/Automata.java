package com.example.automata_backend.automata;

import java.util.Map;
import java.util.Set;

public interface Automata<T> {

    Set<Character> getAlphabet();

    T getStartState();

    Set<T> getAcceptingStates();

    Set<T> getStates();

    Map<T, Map<Character, Set<T>>> getTransitionMap();

}
