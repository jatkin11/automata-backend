package com.example.automata_backend.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.automata_backend.automata.DFA;
import com.example.automata_backend.automata.NFA;
import com.example.automata_backend.dto.ReactFlowEdge;
import com.example.automata_backend.dto.ReactFlowGraph;
import com.example.automata_backend.dto.ReactFlowNode;

public final class ReactFlowReverseMapper {

    private static final char EPSILON = 'ε';

    private ReactFlowReverseMapper() {
    }

    public static NFA toNfa(ReactFlowGraph graph) {
        Set<Integer> states = new LinkedHashSet<>();
        Set<Integer> acceptingStates = new LinkedHashSet<>();
        Set<Character> alphabet = new LinkedHashSet<>();
        Map<Integer, Map<Character, Set<Integer>>> transitions = new LinkedHashMap<>();

        Integer startState = null;

        for (ReactFlowNode node : graph.getNodes()) {
            int state = parseStateId(node.getId());

            states.add(state);
            transitions.putIfAbsent(state, new LinkedHashMap<>());

            if (node.getData() != null && node.getData().isAccepting()) {
                acceptingStates.add(state);
            }

            if (node.getData() != null && node.getData().isStart()) {
                if (startState != null) {
                    throw new IllegalArgumentException("NFA must have exactly one start state.");
                }

                startState = state;
            }
        }

        if (startState == null) {
            throw new IllegalArgumentException("NFA must have a start state.");
        }

        for (ReactFlowEdge edge : graph.getEdges()) {
            int from = parseStateId(edge.getSource());
            int to = parseStateId(edge.getTarget());

            for (char symbol : parseEdgeLabel(edge.getLabel())) {
                transitions
                        .computeIfAbsent(from, ignored -> new LinkedHashMap<>())
                        .computeIfAbsent(symbol, ignored -> new LinkedHashSet<>())
                        .add(to);

                if (symbol != EPSILON) {
                    alphabet.add(symbol);
                }
            }
        }

        return new NFA(
                startState,
                states,
                acceptingStates,
                alphabet,
                transitions
        );
    }

    public static DFA toDfa(ReactFlowGraph graph) {
        Set<Set<Integer>> states = new LinkedHashSet<>();
        Set<Set<Integer>> acceptingStates = new LinkedHashSet<>();
        Set<Character> alphabet = new LinkedHashSet<>();
        Map<Set<Integer>, Map<Character, Set<Set<Integer>>>> transitions = new LinkedHashMap<>();

        Map<String, Set<Integer>> stateByNodeId = new HashMap<>();

        Set<Integer> startState = null;

        for (ReactFlowNode node : graph.getNodes()) {
            int stateNumber = parseStateId(node.getId());
            Set<Integer> dfaState = Set.of(stateNumber);

            states.add(dfaState);
            stateByNodeId.put(node.getId(), dfaState);
            transitions.putIfAbsent(dfaState, new LinkedHashMap<>());

            if (node.getData() != null && node.getData().isAccepting()) {
                acceptingStates.add(dfaState);
            }

            if (node.getData() != null && node.getData().isStart()) {
                if (startState != null) {
                    throw new IllegalArgumentException("DFA must have exactly one start state.");
                }

                startState = dfaState;
            }
        }

        if (startState == null) {
            throw new IllegalArgumentException("DFA must have a start state.");
        }

        for (ReactFlowEdge edge : graph.getEdges()) {
            Set<Integer> from = stateByNodeId.get(edge.getSource());
            Set<Integer> to = stateByNodeId.get(edge.getTarget());

            if (from == null || to == null) {
                throw new IllegalArgumentException(
                        "Edge refers to a node that does not exist: "
                                + edge.getSource() + " -> " + edge.getTarget()
                );
            }

            for (char symbol : parseEdgeLabel(edge.getLabel())) {
                if (symbol == EPSILON) {
                    throw new IllegalArgumentException("DFA cannot contain epsilon transitions.");
                }

                alphabet.add(symbol);

                Map<Character, Set<Set<Integer>>> outgoingTransitions =
                        transitions.computeIfAbsent(from, ignored -> new LinkedHashMap<>());

                Set<Set<Integer>> existingTargets = outgoingTransitions.get(symbol);

                if (existingTargets != null && !existingTargets.equals(Set.of(to))) {
                    throw new IllegalArgumentException(
                            "DFA has nondeterministic transition from "
                                    + edge.getSource()
                                    + " on symbol "
                                    + symbol
                    );
                }

                outgoingTransitions.put(symbol, Set.of(to));
            }
        }

        return new DFA(
                startState,
                states,
                acceptingStates,
                alphabet,
                transitions
        );
    }

    private static int parseStateId(String nodeId) {
        if (nodeId == null || nodeId.isBlank()) {
            throw new IllegalArgumentException("Node id cannot be empty.");
        }

        String numericPart = nodeId.trim();

        if (numericPart.startsWith("q")) {
            numericPart = numericPart.substring(1);
        }

        try {
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Node id must look like q0, q1, q2, etc. Invalid id: " + nodeId
            );
        }
    }

    private static List<Character> parseEdgeLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Edge label cannot be empty.");
        }

        List<Character> symbols = new ArrayList<>();

        String[] parts = label.split(",");

        for (String part : parts) {
            String trimmed = part.trim();

            if (trimmed.equals("ε")) {
                symbols.add(EPSILON);
            } else if (trimmed.length() == 1) {
                symbols.add(trimmed.charAt(0));
            } else {
                throw new IllegalArgumentException(
                        "Each edge label must be one character, ε, or comma-separated symbols. Invalid label: "
                                + label
                );
            }
        }

        return symbols;
    }
}