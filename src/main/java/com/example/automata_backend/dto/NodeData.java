package com.example.automata_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeData {

    private String label;
    private boolean start;
    private boolean accepting;

    public NodeData() {
    }

    public NodeData(String label, boolean start, boolean accepting) {
        this.label = label;
        this.start = start;
        this.accepting = accepting;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isAccepting() {
        return accepting;
    }

    public void setAccepting(boolean accepting) {
        this.accepting = accepting;
    }
}