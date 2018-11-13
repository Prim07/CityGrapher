package com.agh.bsct.datacollector.entities.graphdata;

import com.agh.bsct.datacollector.entities.citydata.Street;

public class Edge {

    private Street street;
    private Integer weight;

    public Edge(Street street, Integer weight) {
        this.street = street;
        this.weight = weight;
    }

    public Street getStreet() {
        return street;
    }
}
