package com.agh.bsct.datacollector.services.parser;

import com.agh.bsct.datacollector.entities.citydata.CityData;
import com.agh.bsct.datacollector.entities.graphdata.GraphData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

@Service
public class DataParser {

    //TODO simple example, build here JSON object representing parser
    public ObjectNode parseToJson(GraphData graphData) {
        //TODO implement when parser in GraphService.returnGraphAsObjectNode is ready
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        graphData.getEdges().forEach((edge) -> objectNode.put(edge.getStreet().getName(),
                                                              edge.getStreet().getNodesIds().toString()));
        return objectNode;
    }

    //TODO simple example, build here JSON object representing parser
    public ObjectNode parseToJson(CityData cityData) {
        //TODO implement when parser in GraphService.returnGraphAsObjectNode is ready
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        cityData.getStreets().forEach((street) -> objectNode.put(street.getName(),
                street.getNodesIds().toString()));
        return objectNode;
    }

}
