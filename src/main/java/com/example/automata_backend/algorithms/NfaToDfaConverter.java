package com.example.automata_backend.algorithms;

import java.util.*;
import com.example.automata_backend.automata.*;

public class NfaToDfaConverter {

    public static final char EPSILON = 'ε';

    public static DFA convert(NFA nfa) {  //NEED TO FIX THIS TO CREATE EMPTY SET AND EACH NODE HAS ARROWS OUT FOR EVERY SYMBOL
        Set<Set<Integer>> dfaStates = new HashSet<>();
        Deque<Set<Integer>> unmarkedStates = new ArrayDeque<>();

        Map<Set<Integer>, Map<Character,Set<Set<Integer>>>> dfaTransitions = new HashMap<>();
        Set<Set<Integer>> dfaAcceptingStates = new HashSet<>();
        Set<Character> dfaAlphabet = nfa.getAlphabet();
        dfaAlphabet.remove(EPSILON);

        Set<Integer> dfaStartingState = EpsilonClosure.epsilonClosure(Set.of(nfa.getStartState()),nfa.getTransitionMap()); //THIS IS TEMP

        dfaStates.add(dfaStartingState);
        unmarkedStates.push(dfaStartingState);

        while(!unmarkedStates.isEmpty()) {
            Set<Integer> currentState = unmarkedStates.pop();
            dfaTransitions.put(currentState, new HashMap<>());

            for (Character x : nfa.getAlphabet()) {
                Set<Integer> moveSet = new HashSet<>();

                for(int nfaState: currentState){
                    moveSet.addAll(nfaTransitionLookup(nfaState, x, nfa.getTransitionMap())); //CHECK FOR SHALLOW COPYING
                }

                Set<Integer> nextDFAState = EpsilonClosure.epsilonClosure(moveSet,nfa.getTransitionMap());

//                if(nextDFAState.isEmpty()){  //THIS IS THE PART THAT NEEDS AMENDING TO ADD THE EMPTY SET, PREVIOUSLY JUST CONTAINED continue;
//                    continue;
//                }

                if(!dfaStates.contains(nextDFAState)){
                    dfaStates.add(nextDFAState);
                    unmarkedStates.add(nextDFAState);
                }

                dfaTransitions.get(currentState).put(x,Set.of(nextDFAState));

            }

        }

        for (Set<Integer> dfaState : dfaStates) { //CHECK THIS WORKS AS EXPECTED
            if (!Collections.disjoint(dfaState, nfa.getAcceptingStates())) {
                dfaAcceptingStates.add(dfaState);
            }
        }

        return new DFA(dfaStartingState,dfaStates,dfaAcceptingStates,dfaAlphabet,dfaTransitions);
    }

    public static Set<Integer> nfaTransitionLookup(int state, char symbol, Map<Integer, Map<Character, Set<Integer>>> nfaTransitionMap){
        if(!nfaTransitionMap.containsKey(state) ||!nfaTransitionMap.get(state).containsKey(symbol)){
            return Collections.emptySet();
        }

        return nfaTransitionMap.get(state).get(symbol);
    }




}