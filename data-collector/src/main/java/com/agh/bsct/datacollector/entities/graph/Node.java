package com.agh.bsct.datacollector.entities.graph;

public class Node {

    private long id;
    private int weight;

    Node(Long id, Integer weight) {
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
