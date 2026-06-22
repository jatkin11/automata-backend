package com.example.automata_backend.algorithms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.automata_backend.automata.NFA;
import com.example.automata_backend.regex.CharacterTokenRegex;
import com.example.automata_backend.regex.ConcatTokenRegex;
import com.example.automata_backend.regex.StarredTokenRegex;
import com.example.automata_backend.regex.TokenisedRegex;
import com.example.automata_backend.regex.UnionTokenRegex;

public class RegexToNfaConverter {



    public static NFA regexToNfaConverterHelper(String regex){

        GlobalStateIDGenerator idGen = new GlobalStateIDGenerator();

        TokenisedRegex tRegex = new RegexTokeniser(regex).tokeniseRegex();

        return convertRegexToNfa(tRegex, idGen);

    }

    public static NFA convertRegexToNfa(TokenisedRegex token, GlobalStateIDGenerator idGen){ //HAVE A LOOK AT IMPROVING DESIGN, COULD DO VISITOR PATTERN
        if(token instanceof CharacterTokenRegex ctr){
            return unaryTokenNfa(ctr.getSingleToken(),idGen);
        }

        if(token instanceof ConcatTokenRegex cntr){
            return concatNfa(convertRegexToNfa(cntr.getLeft(),idGen),convertRegexToNfa(cntr.getRight(),idGen));
        }

        if(token instanceof UnionTokenRegex utr){
            return unionNFA(convertRegexToNfa(utr.getLeft(),idGen),convertRegexToNfa(utr.getRight(),idGen),idGen);
        }

        if(token instanceof StarredTokenRegex str){
            return starredNFA(convertRegexToNfa(str.getStarredToken(),idGen),idGen);
        }

        throw new IllegalArgumentException("invalid regex");
    }

    public static NFA unaryTokenNfa(char c, GlobalStateIDGenerator idGen){ // NEED TO FIX SHALLOW COPYING
        int start = idGen.next();
        int accept = idGen.next();

        Set<Integer> states = new HashSet<>();
        states.add(start);
        states.add(accept);

        Set<Integer> acceptingStates = new HashSet<>();
        acceptingStates.add(accept);

        Set<Character> alphabet = new HashSet<>();
        alphabet.add(c);

        Map<Integer, Map<Character,Set<Integer>>> transitions = new HashMap<>();

        addTransition(transitions,start,c,accept);

        return new NFA(start, states, acceptingStates, alphabet, transitions);

    }

    public static NFA concatNfa(NFA nfa1, NFA nfa2){  // NEED TO FIX SHALLOW COPYING
        Set<Integer> states = new HashSet<>();
        states.addAll(nfa1.getStates());
        states.addAll(nfa2.getStates());

        Set<Character> alphabet = new HashSet<>();
        alphabet.addAll(nfa1.getAlphabet());
        alphabet.addAll(nfa2.getAlphabet());

        Map<Integer, Map<Character,Set<Integer>>> transitions = new HashMap<>();

        transitions.putAll(nfa1.getTransitionMap());
        transitions.putAll(nfa2.getTransitionMap());

        for(Integer i : nfa1.getAcceptingStates()){
            addTransition(transitions,i,'ε',nfa2.getStartState());
        }

        int startState = nfa1.getStartState();
        Set<Integer> acceptingStates = nfa2.getAcceptingStates();

        return new NFA(startState,states,acceptingStates, alphabet,transitions);
    }



    public static NFA unionNFA(NFA nfa1, NFA nfa2, GlobalStateIDGenerator idGen){ // NEED TO FIX SHALLOW COPYING
        int newStart = idGen.next();
        int newAccept = idGen.next();
        Set<Integer> newStates = new HashSet<>();
        newStates.add(newStart);
        newStates.add(newAccept);

        Set<Integer> states = new HashSet<>();
        states.addAll(nfa1.getStates());
        states.addAll(nfa2.getStates());
        states.addAll(newStates);

        Set<Character> alphabet = new HashSet<>();
        alphabet.addAll(nfa1.getAlphabet());
        alphabet.addAll(nfa2.getAlphabet());

        Map<Integer, Map<Character,Set<Integer>>> transitions = new HashMap<>();
        transitions.putAll(nfa1.getTransitionMap());
        transitions.putAll(nfa2.getTransitionMap());

        addTransition(transitions, newStart,'ε', nfa1.getStartState());
        addTransition(transitions,newStart,'ε', nfa2.getStartState());

        for(Integer i: nfa1.getAcceptingStates()){
            addTransition(transitions,i,'ε',newAccept);
        }

        for(Integer j: nfa2.getAcceptingStates()){
            addTransition(transitions,j,'ε',newAccept);
        }

        Set<Integer> acceptingStates = new HashSet<>();
        acceptingStates.add(newAccept);

        return new NFA(newStart, states, acceptingStates,alphabet,transitions);
    }




    public static NFA starredNFA (NFA nfa, GlobalStateIDGenerator idGen){ // NEED TO FIX SHALLOW COPYING
        int newStart = idGen.next();
        int newAccept = idGen.next();
        Set<Integer> acceptingStates = new HashSet<>();
        acceptingStates.add(newAccept);
        Set<Integer> newStates = new HashSet<>();
        newStates.add(newStart);
        newStates.add(newAccept);

        Set<Integer> states = new HashSet<>();
        states.addAll(nfa.getStates());
        states.addAll(newStates);

        Set<Character> alphabet = new HashSet<>(nfa.getAlphabet());

        Map<Integer, Map<Character,Set<Integer>>> transitions = new HashMap<>(nfa.getTransitionMap());

        addTransition(transitions, newStart, 'ε',nfa.getStartState());
        addTransition(transitions,newStart,'ε', newAccept);

        for(Integer i: nfa.getAcceptingStates()){
            addTransition(transitions,i,'ε',nfa.getStartState());
            addTransition(transitions,i,'ε',newAccept);
        }

        return new NFA(newStart,states, acceptingStates,alphabet,transitions);

    }


    private static void addTransition(Map<Integer, Map<Character, Set<Integer>>> transitions, int from, char symbol, int to) {
        transitions.computeIfAbsent(from, k -> new HashMap<>())
                .computeIfAbsent(symbol, k -> new HashSet<>())
                .add(to);
    }




}