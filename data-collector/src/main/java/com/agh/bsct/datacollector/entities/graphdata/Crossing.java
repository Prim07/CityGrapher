package com.agh.bsct.datacollector.entities.graphdata;

import com.agh.bsct.datacollector.entities.citydata.Node;

public class Crossing {

    private Node node;
    private Integer weight;

    public Crossing(Node node, Integer weight) {
        this.node = node;
        this.weight = weight;
    }

    public Node getNode() {
        return node;
    }

}
