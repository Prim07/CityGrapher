package com.agh.bsct.datacollector.entities.citydata;

import java.util.List;

public class CityData {
    private List<Node> nodes;
    private List<Street> streets;

    public CityData(List<Node> nodes, List<Street> streets) {
        this.nodes = nodes;
        this.streets = streets;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
