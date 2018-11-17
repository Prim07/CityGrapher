package com.agh.bsct.datacollector.entities.graph;

public class Edge {

    private Node endNode;
    private double weight;

    Edge(Node endNode, double weight) {
        this.endNode = endNode;
        this.weight = weight;
    }

    public Node getEndNode() {
        return endNode;
    }

    public double getWeight() {
        return weight;
    }
}
