package com.example.automata_backend.algorithms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.automata_backend.automata.NFA;
import com.example.automata_backend.regex.CharacterTokenRegex;
import com.example.automata_backend.regex.ConcatTokenRegex;
import com.example.automata_backend.regex.EmptyTokenRegex;
import com.example.automata_backend.regex.StarredTokenRegex;
import com.example.automata_backend.regex.TokenisedRegex;
import com.example.automata_backend.regex.UnionTokenRegex;

public final class GlushkovRegexToNfaConverter {

    private GlushkovRegexToNfaConverter() {
    }

    public static NFA convert(TokenisedRegex regex) {
        BuildContext context = new BuildContext();

        Analysis analysis = analyse(regex, context);

        Set<Integer> states = new HashSet<>();
        Set<Integer> acceptingStates = new HashSet<>();
        Set<Character> alphabet = new HashSet<>(context.positionToSymbol.values());

        Map<Integer, Map<Character, Set<Integer>>> transitions = new HashMap<>();

        int startState = 0;
        states.add(startState);
        states.addAll(context.positionToSymbol.keySet());

        /*
         * From the special start state, add transitions to every first position.
         *
         * The transition label is the symbol stored at the target position.
         */
        for (Integer firstPosition : analysis.firstPos) {
            Character symbol = context.positionToSymbol.get(firstPosition);
            addTransition(transitions, startState, symbol, firstPosition);
        }

        /*
         * For every followpos relation:
         *
         * If position j can follow position i,
         * then add transition:
         *
         * i --symbol(j)--> j
         */
        for (Map.Entry<Integer, Set<Integer>> entry : context.followPos.entrySet()) {
            Integer fromPosition = entry.getKey();

            for (Integer toPosition : entry.getValue()) {
                Character symbol = context.positionToSymbol.get(toPosition);
                addTransition(transitions, fromPosition, symbol, toPosition);
            }
        }

        /*
         * The accepting states are all positions that can appear last.
         */
        acceptingStates.addAll(analysis.lastPos);

        /*
         * If the regex can accept ε, then the start state is also accepting.
         */
        if (analysis.nullable) {
            acceptingStates.add(startState);
        }

        return new NFA(
                                startState,
                                states,
                acceptingStates,
                alphabet,
                transitions
        );
    }

    private static Analysis analyse(TokenisedRegex regex, BuildContext context) {

        if (regex instanceof CharacterTokenRegex characterRegex) {
            int position = context.newPosition(getCharacter(characterRegex));

            Set<Integer> firstPos = new HashSet<>();
            Set<Integer> lastPos = new HashSet<>();

            firstPos.add(position);
            lastPos.add(position);

            return new Analysis(false, firstPos, lastPos);
        }

        if (regex instanceof EmptyTokenRegex) {
            return new Analysis(false, new HashSet<>(), new HashSet<>());
        }

        if (regex instanceof UnionTokenRegex unionRegex) {
            Analysis left = analyse(getLeft(unionRegex), context);
            Analysis right = analyse(getRight(unionRegex), context);

            Set<Integer> firstPos = union(left.firstPos, right.firstPos);
            Set<Integer> lastPos = union(left.lastPos, right.lastPos);

            boolean nullable = left.nullable || right.nullable;

            return new Analysis(nullable, firstPos, lastPos);
        }

        if (regex instanceof ConcatTokenRegex concatRegex) {
            Analysis left = analyse(getLeft(concatRegex), context);
            Analysis right = analyse(getRight(concatRegex), context);

            /*
             * For concatenation AB:
             *
             * firstpos(AB):
             * - firstpos(A)
             * - plus firstpos(B), if A is nullable
             */
            Set<Integer> firstPos = new HashSet<>(left.firstPos);

            if (left.nullable) {
                firstPos.addAll(right.firstPos);
            }

            /*
             * lastpos(AB):
             * - lastpos(B)
             * - plus lastpos(A), if B is nullable
             */
            Set<Integer> lastPos = new HashSet<>(right.lastPos);

            if (right.nullable) {
                lastPos.addAll(left.lastPos);
            }

            /*
             * Every position in lastpos(A) can be followed by
             * every position in firstpos(B).
             */
            for (Integer leftLastPosition : left.lastPos) {
                context.addFollowPositions(leftLastPosition, right.firstPos);
            }

            boolean nullable = left.nullable && right.nullable;

            return new Analysis(nullable, firstPos, lastPos);
        }

        if (regex instanceof StarredTokenRegex starredRegex) {
            Analysis inner = analyse(getInner(starredRegex), context);

            /*
             * For A*:
             *
             * Every position in lastpos(A) can be followed by
             * every position in firstpos(A).
             */
            for (Integer lastPosition : inner.lastPos) {
                context.addFollowPositions(lastPosition, inner.firstPos);
            }

            /*
             * A* is always nullable.
             */
            return new Analysis(
                    true,
                    new HashSet<>(inner.firstPos),
                    new HashSet<>(inner.lastPos)
            );
        }

        throw new IllegalArgumentException(
                "Unsupported regex token type: " + regex.getClass().getName()
        );
    }

    private static void addTransition(
            Map<Integer, Map<Character, Set<Integer>>> transitions,
            Integer from,
            Character symbol,
            Integer to
    ) {
        transitions
                .computeIfAbsent(from, key -> new HashMap<>())
                .computeIfAbsent(symbol, key -> new HashSet<>())
                .add(to);
    }

    private static Set<Integer> union(Set<Integer> a, Set<Integer> b) {
        Set<Integer> result = new HashSet<>(a);
        result.addAll(b);
        return result;
    }

    private static final class BuildContext {
        private int nextPosition = 1;

        private final Map<Integer, Character> positionToSymbol = new HashMap<>();
        private final Map<Integer, Set<Integer>> followPos = new HashMap<>();

        private int newPosition(Character symbol) {
            int position = nextPosition;
            nextPosition++;

            positionToSymbol.put(position, symbol);

            return position;
        }

        private void addFollowPositions(Integer fromPosition, Set<Integer> toPositions) {
            followPos
                    .computeIfAbsent(fromPosition, key -> new HashSet<>())
                    .addAll(toPositions);
        }
    }

    private static final class Analysis {
        private final boolean nullable;
        private final Set<Integer> firstPos;
        private final Set<Integer> lastPos;

        private Analysis(boolean nullable, Set<Integer> firstPos, Set<Integer> lastPos) {
            this.nullable = nullable;
            this.firstPos = firstPos;
            this.lastPos = lastPos;
        }
    }
        /*
     * These helper methods are here so you only need to change this section
     * if your regex token classes use different getter names.
     */

    private static Character getCharacter(CharacterTokenRegex regex) {
        return regex.getSingleToken();
    }

    private static TokenisedRegex getLeft(UnionTokenRegex regex) {
        return regex.getLeft();
    }

    private static TokenisedRegex getRight(UnionTokenRegex regex) {
        return regex.getRight();
    }

    private static TokenisedRegex getLeft(ConcatTokenRegex regex) {
        return regex.getLeft();
    }

    private static TokenisedRegex getRight(ConcatTokenRegex regex) {
        return regex.getRight();
    }

    private static TokenisedRegex getInner(StarredTokenRegex regex) {
        return regex.getStarredToken();
    }
}