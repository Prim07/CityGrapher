package com.agh.bsct.datacollector.entities.graphdata;

import com.agh.bsct.datacollector.entities.citydata.CityData;

import java.util.List;

public class GraphData {
    private List<Edge> edges;
    private List<Crossing> crossings;

    public GraphData(CityData cityData) {
        //TODO impl
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
