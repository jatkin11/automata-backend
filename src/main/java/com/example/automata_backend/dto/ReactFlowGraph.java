package com.example.automata_backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReactFlowGraph {
    private String automataType;
    private List<ReactFlowNode> nodes;
    private List<ReactFlowEdge> edges;

    public ReactFlowGraph() {
    }

    public ReactFlowGraph(List<ReactFlowNode> nodes, List<ReactFlowEdge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }


    public ReactFlowGraph(String automataType, List<ReactFlowNode> nodes, List<ReactFlowEdge> edges) {
        this.automataType = automataType;
        this.nodes = nodes;
        this.edges = edges;
    }

    public String getAutomataType() {
        return automataType;
    }

    public void setAutomataType(String automataType) {
        this.automataType = automataType;
    }

    public List<ReactFlowNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ReactFlowNode> nodes) {
        this.nodes = nodes;
    }

    public List<ReactFlowEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<ReactFlowEdge> edges) {
        this.edges = edges;
    }
}
