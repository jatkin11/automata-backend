package com.example.automata_backend.algorithms;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EpsilonClosure {

    public static Set<Integer> epsilonClosure(Set<Integer> states, Map<Integer, Map<Character,Set<Integer>>> transitions){ //NEED TO ADD FULL EPSILON CLOSURE
        Set<Integer> closure = new HashSet<>();

        for(Integer i: states){
            closure.addAll(epsilonClosurePerState(i, transitions));
        }

        return closure;

    }

    public static Set<Integer> epsilonClosurePerState(Integer state, Map<Integer,Map<Character,Set<Integer>>> transitions){
        Set<Integer> closurePerState = new HashSet<>();
        Deque<Integer> stack = new ArrayDeque<>();

        closurePerState.add(state);
        stack.add(state);

        while(!stack.isEmpty()){
            int currentState = stack.pop();

            for(Integer i: transitions.getOrDefault(currentState, Collections.emptyMap()).getOrDefault('ε',Collections.emptySet())){  //NEED TO ADD BETTER VALIDATION HERE
                if(!closurePerState.contains(i)){
                    closurePerState.add(i);
                    stack.add(i);
                }
            }
        }
        return closurePerState;

    }

}
