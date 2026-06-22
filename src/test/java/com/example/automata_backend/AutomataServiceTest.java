package com.example.automata_backend;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.example.automata_backend.dto.NodeData;
import com.example.automata_backend.dto.Position;
import com.example.automata_backend.dto.ReactFlowEdge;
import com.example.automata_backend.dto.ReactFlowGraph;
import com.example.automata_backend.dto.ReactFlowNode;
import com.example.automata_backend.service.AutomataService;

class AutomataServiceTest {

    @Test
    void convertsSimpleNfaToDfa() {
        AutomataService service = new AutomataService();

        ReactFlowGraph graph = new ReactFlowGraph();
        graph.setAutomataType("NFA");

        ReactFlowNode q0 = new ReactFlowNode();
        q0.setId("q0");
        q0.setPosition(new Position(100, 100));
        q0.setData(new NodeData("q0", true, false));

        ReactFlowNode q1 = new ReactFlowNode();
        q1.setId("q1");
        q1.setPosition(new Position(300, 100));
        q1.setData(new NodeData("q1", false, true));

        ReactFlowEdge edge1 = new ReactFlowEdge();
        edge1.setId("q0-q0-a");
        edge1.setSource("q0");
        edge1.setTarget("q0");
        edge1.setLabel("a");

        ReactFlowEdge edge2 = new ReactFlowEdge();
        edge2.setId("q0-q1-a");
        edge2.setSource("q0");
        edge2.setTarget("q1");
        edge2.setLabel("a");

        graph.setNodes(List.of(q0, q1));
        graph.setEdges(List.of(edge1, edge2));

        ReactFlowGraph result = service.convertToDfa(graph);

        assertNotNull(result);
        assertNotNull(result.getNodes());
        assertNotNull(result.getEdges());

        System.out.println("Converted DFA graph:");
        System.out.println(result);

        assertEquals("DFA", result.getAutomataType());

        assertTrue(
                result.getNodes().stream()
                        .anyMatch(node -> node.getData().getLabel().contains("q0")
                                && node.getData().getLabel().contains("q1")),
                "DFA should contain a combined state containing q0 and q1"
        );

        assertTrue(
                result.getNodes().stream()
                        .anyMatch(node -> node.getData().isAccepting()),
                "DFA should contain at least one accepting state"
        );

        assertTrue(
                result.getEdges().stream()
                        .anyMatch(edge -> "a".equals(edge.getLabel())),
                "DFA should contain an edge labelled a"
        );
    }
}