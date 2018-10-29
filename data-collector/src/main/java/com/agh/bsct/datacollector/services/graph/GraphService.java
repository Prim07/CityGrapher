package com.agh.bsct.datacollector.services.graph;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;

@Service
public class GraphService {

    private GraphParser graphParser;

    @Autowired
    public GraphService(GraphParser graphParser) {
        this.graphParser = graphParser;
    }

    public ObjectNode createGraph(HashMap<String, LinkedList<Long>> streetNameToNodes) {
        //TODO implement
        return graphParser.parseToJson(streetNameToNodes);
    }
}
