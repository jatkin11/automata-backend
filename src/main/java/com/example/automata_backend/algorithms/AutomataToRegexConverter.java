package com.example.automata_backend.algorithms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.example.automata_backend.automata.Automata;
import com.example.automata_backend.regex.CharacterTokenRegex;
import com.example.automata_backend.regex.ConcatTokenRegex;
import com.example.automata_backend.regex.EmptyTokenRegex;
import com.example.automata_backend.regex.EpsilonTokenRegex;
import com.example.automata_backend.regex.StarredTokenRegex;
import com.example.automata_backend.regex.TokenisedRegex;
import com.example.automata_backend.regex.UnionTokenRegex;

public class AutomataToRegexConverter {

    public static final char EPSILON = 'ε';

    public static <T> TokenisedRegex convert(Automata<T> automata) {

        GlobalStateIDGenerator idGen = new GlobalStateIDGenerator();

        Map<T, Integer> stateIDs = new LinkedHashMap<>();

        // Give every original automata state a fresh integer ID
        for (T state : automata.getStates()) {
            stateIDs.put(state, idGen.next());
        }

        int newStartState = idGen.next();
        int newAcceptState = idGen.next();

        Set<Integer> regexStates = new LinkedHashSet<>();
        Set<Integer> eliminableStates = new LinkedHashSet<>();

        regexStates.addAll(stateIDs.values());
        eliminableStates.addAll(stateIDs.values());

        regexStates.add(newStartState);
        regexStates.add(newAcceptState);

        Map<Integer, Map<Integer, TokenisedRegex>> tokenisedRegexMap =
                new HashMap<>();

        // Add ε transition from new start to old start
        int oldStartState = stateIDs.get(automata.getStartState());

        addTransitionToRegexMap(
                tokenisedRegexMap,
                newStartState,
                oldStartState,
                new EpsilonTokenRegex()
        );

        // Add ε transitions from old accepting states to new accept
        for (T acceptingState : automata.getAcceptingStates()) {
            int acceptingStateId = stateIDs.get(acceptingState);

            addTransitionToRegexMap(
                    tokenisedRegexMap,
                    acceptingStateId,
                    newAcceptState,
                    new EpsilonTokenRegex()
            );
        }

        // Copy original automata transitions into the regex map
        for (Map.Entry<T, Map<Character, Set<T>>> fromEntry
                : automata.getTransitionMap().entrySet()) {

            T fromState = fromEntry.getKey();
            int fromId = stateIDs.get(fromState);

            for (Map.Entry<Character, Set<T>> symbolEntry
                    : fromEntry.getValue().entrySet()) {

                char symbol = symbolEntry.getKey();

                TokenisedRegex newToken;

                if (symbol == EPSILON) {
                    newToken = new EpsilonTokenRegex();
                } else {
                    newToken = new CharacterTokenRegex(symbol);
                }

                for (T toState : symbolEntry.getValue()) {
                    int toId = stateIDs.get(toState);

                    TokenisedRegex existingRegex =
                            getRegex(tokenisedRegexMap, fromId, toId);

                    TokenisedRegex updatedRegex =
                            union(existingRegex, newToken);

                    addTransitionToRegexMap(
                            tokenisedRegexMap,
                            fromId,
                            toId,
                            updatedRegex
                    );
                }
            }
        }

        // State elimination
        for (int k : new HashSet<>(eliminableStates)) {

            for (int i : new HashSet<>(regexStates)) {
                if (i == k) {
                    continue;
                }

                for (int j : new HashSet<>(regexStates)) {
                    if (j == k) {
                        continue;
                    }

                    TokenisedRegex ik = getRegex(tokenisedRegexMap, i, k);
                    TokenisedRegex kj = getRegex(tokenisedRegexMap, k, j);

                    // If there is no path i -> k or no path k -> j,
                    // then going through k is impossible.
                    if (isEmpty(ik) || isEmpty(kj)) {
                        continue;
                    }

                    TokenisedRegex direct =
                            getRegex(tokenisedRegexMap, i, j);

                    TokenisedRegex kk =
                            getRegex(tokenisedRegexMap, k, k);

                    TokenisedRegex throughK =
                            concat(
                                    concat(
                                            ik,
                                            star(kk)
                                    ),
                                    kj
                            );

                    TokenisedRegex updated =
                            union(direct, throughK);

                    addTransitionToRegexMap(
                            tokenisedRegexMap,
                            i,
                            j,
                            updated
                    );
                }
            }

            removeState(tokenisedRegexMap, regexStates, k);
        }

        return getRegex(tokenisedRegexMap, newStartState, newAcceptState);
    }

    private static void addTransitionToRegexMap(
            Map<Integer, Map<Integer, TokenisedRegex>> regexMap,
            int from,
            int to,
            TokenisedRegex regex
    ) {
        regexMap.computeIfAbsent(from, key -> new HashMap<>())
                .put(to, regex);
    }

    private static TokenisedRegex getRegex(
            Map<Integer, Map<Integer, TokenisedRegex>> map,
            int from,
            int to
    ) {
        if (!map.containsKey(from)) {
            return new EmptyTokenRegex();
        }

        return map.get(from).getOrDefault(to, new EmptyTokenRegex());
    }

    private static void removeState(
            Map<Integer, Map<Integer, TokenisedRegex>> map,
            Set<Integer> states,
            int stateToRemove
    ) {
        map.remove(stateToRemove);

        for (Map<Integer, TokenisedRegex> innerMap : map.values()) {
            innerMap.remove(stateToRemove);
        }

        states.remove(stateToRemove);
    }

    private static boolean isEmpty(TokenisedRegex regex) {
        return regex instanceof EmptyTokenRegex;
    }

    private static boolean isEpsilon(TokenisedRegex regex) {
        return regex instanceof EpsilonTokenRegex;
    }

    private static TokenisedRegex union(
            TokenisedRegex left,
            TokenisedRegex right
    ) {
        if (isEmpty(left)) {
            return right;
        }

        if (isEmpty(right)) {
            return left;
        }

        return new UnionTokenRegex(left, right);
    }

    private static TokenisedRegex concat(
            TokenisedRegex left,
            TokenisedRegex right
    ) {
        if (isEmpty(left) || isEmpty(right)) {
            return new EmptyTokenRegex();
        }

        if (isEpsilon(left)) {
            return right;
        }

        if (isEpsilon(right)) {
            return left;
        }

        return new ConcatTokenRegex(left, right);
    }

    private static TokenisedRegex star(TokenisedRegex regex) {
        if (isEmpty(regex) || isEpsilon(regex)) {
            return new EpsilonTokenRegex();
        }

        if (regex instanceof StarredTokenRegex) {
            return regex;
        }

        return new StarredTokenRegex(regex);
    }
}