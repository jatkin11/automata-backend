package com.example.automata_backend.automata;

import java.util.Map;
import java.util.Set;

public class DFA implements Automata<Set<Integer>> {
    private final Set<Set<Integer>> states;
    private final  Set<Set<Integer>> acceptingStates;
    private final  Set<Character> alphabet;
    private final  Set<Integer> startState;
    private final  Map<Set<Integer>, Map<Character,Set<Set<Integer>>>> transitionMap;

    public DFA(Set<Integer> startState, Set<Set<Integer>> states, Set<Set<Integer>> acceptingStates, Set<Character> alphabet, Map<Set<Integer>, Map<Character,Set<Set<Integer>>>> transitions){
        this.startState = startState;
        this.states = states;
        this.acceptingStates = acceptingStates;
        this.alphabet = alphabet;
        this.transitionMap = transitions;
    }

    @Override
    public Set<Character> getAlphabet() {
        return alphabet;
    }

    @Override
    public Set<Integer> getStartState() {
        return startState;
    }

    @Override
    public Set<Set<Integer>> getAcceptingStates() {
        return acceptingStates;
    }

    @Override
    public Set<Set<Integer>> getStates() {
        return states;
    }

    @Override
    public Map<Set<Integer>, Map<Character, Set<Set<Integer>>>> getTransitionMap() {
        return transitionMap;
    }
}