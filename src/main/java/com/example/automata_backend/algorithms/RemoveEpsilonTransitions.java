package com.example.automata_backend.algorithms;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.automata_backend.automata.NFA;

public class RemoveEpsilonTransitions {
    
    public static NFA removeEpsilonTransitions(NFA nfa) {
        Set<Integer> newStates = new HashSet<>(nfa.getStates());
        Set<Character> newAlphabet = new HashSet<>(nfa.getAlphabet());
        newAlphabet.remove(EpsilonClosure.EPSILON);

        Set<Integer> newAcceptingStates = new HashSet<>();
        Map<Integer, Map<Character, Set<Integer>>> newTransitions = new HashMap<>();

        for (Integer state : nfa.getStates()) {

            Set<Integer> closure =
                    EpsilonClosure.epsilonClosurePerState(state, nfa.getTransitionMap());

            // If this state's ε-closure reaches an accepting state,
            // then this state becomes accepting.
            for (Integer closureState : closure) {
                if (nfa.getAcceptingStates().contains(closureState)) {
                    newAcceptingStates.add(state);
                    break;
                }
            }

            for (Character symbol : newAlphabet) {
                Set<Integer> symbolTargets = new HashSet<>();

                // From anywhere in ε-closure(state),
                // follow the real symbol transition.
                for (Integer closureState : closure) {
                    Set<Integer> directTargets = nfa.getTransitionMap()
                            .getOrDefault(closureState, Collections.emptyMap())
                            .getOrDefault(symbol, Collections.emptySet());

                    symbolTargets.addAll(directTargets);
                }

                // Then take ε-closure of those targets too.
                Set<Integer> finalTargets =
                        EpsilonClosure.epsilonClosure(symbolTargets, nfa.getTransitionMap());

                if (!finalTargets.isEmpty()) {
                    newTransitions
                            .computeIfAbsent(state, k -> new HashMap<>())
                            .put(symbol, finalTargets);
                }
            }
        }

        return new NFA(
                nfa.getStartState(),
                newStates,
                newAcceptingStates,
                newAlphabet,
                newTransitions
        );
        }

}