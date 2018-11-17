package com.agh.bsct.datacollector.entities.graphdata;

import com.agh.bsct.datacollector.entities.citydata.Street;

public class Edge {

    private Street street;
    private double weight;

    public Edge(Street street, double weight) {
        this.street = street;
        this.weight = weight;
    }

    public Street getStreet() {
        return street;
    }

    public double getWeight() {
        return weight;
    }
}
