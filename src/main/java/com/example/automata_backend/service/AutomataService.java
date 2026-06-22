package com.example.automata_backend.service;

import org.springframework.stereotype.Service;

import com.example.automata_backend.automata.DFA;
import com.example.automata_backend.automata.NFA;
import com.example.automata_backend.dto.ReactFlowGraph;
import com.example.automata_backend.mapper.ReactFlowReverseMapper;

@Service
public class AutomataService {

    public ReactFlowGraph convertToDfa(ReactFlowGraph graph) {
        NFA nfa = ReactFlowReverseMapper.toNfa(graph);

        // Later:
        // DFA dfa = NfaToDfaConverter.convert(nfa);
        // return ReactFlowMapper.fromAutomata(dfa);

        return graph;
    }

    public String convertToRegex(ReactFlowGraph graph) {
        if ("DFA".equalsIgnoreCase(graph.getAutomataType())) {
            DFA dfa = ReactFlowReverseMapper.toDfa(graph);

            // Later:
            // return AutomataToRegexConverter.convert(dfa);

            return "(placeholder-regex-from-dfa)";
        }

        NFA nfa = ReactFlowReverseMapper.toNfa(graph);

        // Later:
        // return AutomataToRegexConverter.convert(nfa);

        return "(placeholder-regex-from-nfa)";
    }
}