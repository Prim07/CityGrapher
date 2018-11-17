package com.agh.bsct.datacollector.entities.graphdata;

import com.agh.bsct.datacollector.entities.citydata.CityData;

import java.util.List;
import java.util.stream.Collectors;

public class GraphData {

    private List<Edge> edges;
    private List<Crossing> crossings;

    public GraphData(List<Edge> edges, List<Crossing> crossings) {
        this.edges = edges;
        this.crossings = crossings;
    }

    public CityData toCityData() {
        return new CityData(crossings.stream()
                                    .map(Crossing::getNode)
                                    .collect(Collectors.toList()),
                            edges.stream()
                                    .map(Edge::getStreet)
                                    .collect(Collectors.toList()));
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Crossing> getCrossings() {
        return crossings;
    }
}
