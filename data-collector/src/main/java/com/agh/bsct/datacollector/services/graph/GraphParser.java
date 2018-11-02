package com.agh.bsct.datacollector.services.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashSet;

@Service
public class GraphParser {

    //TODO simple example, build here JSON object representing graph
    ObjectNode parseToJson(HashMap<String, LinkedHashSet<Long>> streetNameToNodes) {
        //TODO implement when graph in GraphService.createGraph is ready
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        streetNameToNodes.forEach((streetName, nodes) -> objectNode.put(streetName, nodes.toString()));
        return objectNode;
    }

}
