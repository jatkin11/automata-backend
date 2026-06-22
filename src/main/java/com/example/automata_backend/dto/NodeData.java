package com.example.automata_backend.dto;

public class NodeData {
    private String label;
    private boolean start;
    private boolean accepting;

    public NodeData(String label, boolean start, boolean accepting){
        this.label = label;
        this.start = start;
        this.accepting = accepting;
    }

    public boolean isAccepting() {
        return accepting;
    }

    public boolean isStart() {
        return start;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setAccepting(boolean accepting) {
        this.accepting = accepting;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
}
