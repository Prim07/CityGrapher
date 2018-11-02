package com.agh.bsct.datacollector.entities.citydata;

import java.util.List;

public class CityData {
    private List<Node> nodes;
    private List<Street> streets;
    private List<Node> crossings;

    public List<Street> getStreets() {
        return streets;
    }
}
