package com.agh.bsct.datacollector.entities.citydata;

import java.util.List;

public class CityData {
    private List<Node> nodes;
    private List<Street> streets;
    private List<Node> crossings;

    public CityData(List<Node> nodes, List<Street> streets, List<Node> crossings) {
        this.nodes = nodes;
        this.streets = streets;
        this.crossings = crossings;
    }

    public List<Street> getStreets() {
        return streets;
    }
}
