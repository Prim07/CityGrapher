package com.agh.bsct.datacollector.services.data;

import com.agh.bsct.datacollector.entities.citydata.CityData;
import com.agh.bsct.datacollector.entities.graphdata.GraphData;
import com.agh.bsct.datacollector.services.parser.DataParser;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GraphDataService {

    private DataParser dataParser;

    @Autowired
    public GraphDataService(DataParser dataParser) {
        this.dataParser = dataParser;
    }

    public GraphData buildGraphData(CityData cityDataWithCrossing) {
        return new GraphData();
    }

    public GraphData calculateEdgeWeights(GraphData cityData) {
        //TODO impl
        return new GraphData();
    }

    public GraphData calculateNodeWeights(GraphData cityData) {
        //TODO impl
        return new GraphData();
    }

    public ObjectNode returnGraphAsObjectNode(GraphData cityData) {
        //TODO implement
        return dataParser.parseToJson(cityData);
    }
}
