package com.example.automata_backend.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.automata_backend.automata.Automata;
import com.example.automata_backend.automata.DFA;
import com.example.automata_backend.automata.NFA;
import com.example.automata_backend.dto.NodeData;
import com.example.automata_backend.dto.Position;
import com.example.automata_backend.dto.ReactFlowEdge;
import com.example.automata_backend.dto.ReactFlowGraph;
import com.example.automata_backend.dto.ReactFlowNode;

public class ReactFlowMapper {

    public static final String EMPTY_SET = "∅";

    public static <T> ReactFlowGraph fromAutomata(Automata<T> automata){

        return new ReactFlowGraph(getAutomataType(automata),toNodes(automata), toEdges(automata));

    }


    public static <T> List<ReactFlowEdge> toEdges(Automata<T> automata){
        List<ReactFlowEdge> edges = new ArrayList<>();

        for(Map.Entry<T, Map<Character, Set<T>>> fromMapEntry : automata.getTransitionMap().entrySet()) {

            T fromNode = fromMapEntry.getKey();

            for(Map.Entry<Character, Set<T>> fromInnerEntry : fromMapEntry.getValue().entrySet()){

                Character symbol = fromInnerEntry.getKey();

                for(T toNode : fromInnerEntry.getValue()){
                    edges.add(new ReactFlowEdge(
                            getEdgeIdString(getNodeIdString(fromNode),getNodeIdString(toNode),symbol),
                            getNodeIdString(fromNode),
                            getNodeIdString(toNode),
                            String.valueOf(symbol),
                            "bezier"
                    ));
                }
            }
        }
        return edges;
    }


public static <T> List<ReactFlowNode> toNodes(Automata<T> automata) {
    return automata.getStates().stream()
            .map(state -> new ReactFlowNode(
                    getNodeIdString(state),
                    new Position(100, 100),
                    new NodeData(
                            getLabelString(state),
                            automata.getStartState().equals(state),
                            automata.getAcceptingStates().contains(state)
                    )
            ))
            .toList();
}


    public static <T> String getNodeIdString(T state){
        if (state instanceof Integer i) {
            return "q" + i;
        }

        if (state instanceof Set<?> set) {
            return "q" + set.stream()
                    .map(Object::toString)
                    .sorted()
                    .reduce((a, b) -> a + "q" + b)
                    .orElse(EMPTY_SET);
        }

        return "q" + String.valueOf(state);
    }


    public static <T> String getLabelString(T state){
        if (state instanceof Set<?> set) {
            if (set.isEmpty()) {
                return "∅";
            }

            return set.stream()
                    .map(Object::toString)
                    .sorted()
                    .map(s -> "q" + s)
                    .reduce((a, b) -> a + "," + b)
                    .map(s -> "{" + s + "}")
                    .orElse(EMPTY_SET);
        }

        return "q" + state;

    }

    public static<T> String getEdgeIdString(String fromNode, String toNode, Character symbol){
        return fromNode+"_"+symbol+"_"+toNode;
    }

    private static String getAutomataType(Automata<?> automata) {
        if (automata instanceof DFA) {
            return "DFA";
        }

        if (automata instanceof NFA) {
            return "NFA";
        }

        return "UNKNOWN";
    }




}