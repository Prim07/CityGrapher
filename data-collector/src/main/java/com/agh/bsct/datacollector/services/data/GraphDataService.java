package com.agh.bsct.datacollector.services.data;

import com.agh.bsct.datacollector.entities.citydata.CityData;
import com.agh.bsct.datacollector.entities.citydata.Node;
import com.agh.bsct.datacollector.entities.citydata.Street;
import com.agh.bsct.datacollector.entities.graphdata.Crossing;
import com.agh.bsct.datacollector.entities.graphdata.Edge;
import com.agh.bsct.datacollector.entities.graphdata.GraphData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GraphDataService {

    public GraphData getGraphData(CityData cityData) {
        List<Edge> edges = calculateEdgeWeights(cityData.getStreets());
        List<Crossing> crossings = calculateNodeWeights(cityData.getNodes());
        return new GraphData(edges, crossings);
    }

    private List<Edge> calculateEdgeWeights(List<Street> streets) {
        return streets.stream()
                .map((street) -> new Edge(street, 1))
                .collect(Collectors.toList());
    }

    private List<Crossing> calculateNodeWeights(List<Node> nodes) {
        return nodes.stream()
                .map((node -> new Crossing(node, 1)))
                .collect(Collectors.toList());
    }

    public List<Node> runAlgorithmAndCalculateHospitalNodes(GraphData graphData) {
        //TODO impl
        return new ArrayList<>();
    }
}
