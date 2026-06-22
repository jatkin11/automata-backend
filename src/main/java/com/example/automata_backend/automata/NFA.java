package com.example.automata_backend.automata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NFA implements Automata<Integer> {

    private final Set<Integer> states;
    private final Set<Integer> acceptingStates;
    private final Set<Character> alphabet;
    private final Integer startState;
    private Map<Integer,Map<Character,Set<Integer>>> transitionMap = new HashMap<>();

    public NFA(Integer startState, Set<Integer> states, Set<Integer> acceptingStates, Set<Character> alphabet, Map<Integer,Map<Character,Set<Integer>>> transitions){
        this.startState = startState;
        this.states = states;
        this.acceptingStates = acceptingStates;
        this.alphabet = alphabet;
        this.transitionMap = transitions;
    }

    @Override
    public Integer getStartState() {
        return startState;
    }

    @Override
    public Set<Character> getAlphabet() {
        return alphabet;
    }

    @Override
    public Set<Integer> getStates() {
        return states;
    }

    @Override
    public Set<Integer> getAcceptingStates() {
        return acceptingStates;
    }

    @Override
    public Map<Integer, Map<Character, Set<Integer>>> getTransitionMap() {
        return transitionMap;
    }
}