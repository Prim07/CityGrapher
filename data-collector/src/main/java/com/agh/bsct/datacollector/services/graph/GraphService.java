package com.agh.bsct.datacollector.services.graph;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GraphService {

    private GraphParser graphParser;

    @Autowired
    public GraphService(GraphParser graphParser) {
        this.graphParser = graphParser;
    }

    public ObjectNode createGraph() {
        return graphParser.parseToJson();
    }
}
