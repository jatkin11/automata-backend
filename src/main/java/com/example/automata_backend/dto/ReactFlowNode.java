
package com.example.automata_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReactFlowNode {

    private String id;
    private Position position;
    private NodeData data;

    public ReactFlowNode() {
    }

    public ReactFlowNode(String id, Position position, NodeData data){
        this.id = id;
        this.position = position;
        this.data = data;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public NodeData getData() {
        return data;
    }

    public void setData(NodeData data) {
        this.data = data;
    }
}