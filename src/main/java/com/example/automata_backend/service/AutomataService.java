package com.example.automata_backend.service;

import org.springframework.stereotype.Service;

import com.example.automata_backend.algorithms.AutomataToRegexConverter;
import com.example.automata_backend.algorithms.GlushkovRegexToNfaConverter;
import com.example.automata_backend.algorithms.NfaToDfaConverter;
import com.example.automata_backend.algorithms.RegexTokeniser;
import com.example.automata_backend.algorithms.RemoveEpsilonTransitions;
import com.example.automata_backend.algorithms.TokenisedRegexToString;
import com.example.automata_backend.automata.DFA;
import com.example.automata_backend.automata.NFA;
import com.example.automata_backend.dto.ReactFlowGraph;
import com.example.automata_backend.mapper.ReactFlowMapper;
import com.example.automata_backend.mapper.ReactFlowReverseMapper;

@Service
public class AutomataService {

    public ReactFlowGraph convertToDfa(ReactFlowGraph graph) {
        NFA nfa = ReactFlowReverseMapper.toNfa(graph);

        DFA dfa = NfaToDfaConverter.convert(nfa);
        return ReactFlowMapper.fromAutomata(dfa);

    }

    public String convertToRegex(ReactFlowGraph graph) {
        if ("DFA".equalsIgnoreCase(graph.getAutomataType())) {
            DFA dfa = ReactFlowReverseMapper.toDfa(graph);
            return TokenisedRegexToString.toRegexString(AutomataToRegexConverter.convert(dfa));
        }

        NFA nfa = ReactFlowReverseMapper.toNfa(graph);

        return TokenisedRegexToString.toRegexString(AutomataToRegexConverter.convert(nfa));
    }

    public ReactFlowGraph convertToNfa(String regexInput){
        //return ReactFlowMapper.fromAutomata(RegexToNfaConverter.regexToNfaConverterHelper(regexInput));
        return ReactFlowMapper.fromAutomata(GlushkovRegexToNfaConverter.convert(new RegexTokeniser(regexInput).tokeniseRegex()));
    }

    public ReactFlowGraph minimiseNfa(ReactFlowGraph graph){
        NFA nfa = RemoveEpsilonTransitions.removeEpsilonTransitions(ReactFlowReverseMapper.toNfa(graph));
        return ReactFlowMapper.fromAutomata(nfa);
    }


}