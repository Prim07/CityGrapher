package com.agh.bsct.datacollector.services.data;

import com.agh.bsct.datacollector.entities.citydata.CityData;
import com.agh.bsct.datacollector.entities.graphdata.GraphData;
import org.springframework.stereotype.Service;

@Service
public class GraphDataService {

    public GraphData getGraphData(CityData cityData) {
        var graphData = new GraphData(cityData);
        graphData = calculateEdgeWeights(graphData);
        graphData = calculateNodeWeights(graphData);
        return graphData;
    }

    private GraphData calculateEdgeWeights(GraphData graphData) {
        //TODO impl
        return graphData;
    }

    private GraphData calculateNodeWeights(GraphData graphData) {
        //TODO impl
        return graphData;
    }
}
