package com.agh.bsct.datacollector.services.graph;

import com.agh.bsct.datacollector.entities.citydata.Street;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GraphService {

    private GraphParser graphParser;

    @Autowired
    public GraphService(GraphParser graphParser) {
        this.graphParser = graphParser;
    }

    public ObjectNode createGraph(Set<Street> streetNameToNodes) {
        //TODO implement
        return graphParser.parseToJson(streetNameToNodes);
    }
}
