package com.example.automata_backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.automata_backend.dto.ReactFlowGraph;

@RestController
@RequestMapping("/api/automata")
@CrossOrigin(origins = "http://localhost:5173")
public class AutomataController {

    @PostMapping("/convert-to-dfa")
    public ReactFlowGraph convertToDfa(@RequestBody ReactFlowGraph graph) {

        System.out.println("Received graph:");
        System.out.println(graph);

        // Later:
        // 1. Convert ReactFlowGraphDto into your NFA object
        // 2. Run NfaToDfaConverter.convert(nfa)
        // 3. Convert DFA back into ReactFlowGraphDto
        // 4. Return it

        return graph; // placeholder for now
    }
}