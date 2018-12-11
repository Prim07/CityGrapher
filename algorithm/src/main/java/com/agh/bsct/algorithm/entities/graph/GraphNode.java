package com.agh.bsct.algorithm.entities.graph;

public class GraphNode {

    private long id;
    private int weight;

    GraphNode(Long id, Integer weight) {
        this.id = id;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public Integer getWeight() {
        return weight;
    }
}
