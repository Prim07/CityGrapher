package com.agh.bsct.datacollector.services.graph;

import com.agh.bsct.datacollector.entities.citydata.Street;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GraphParser {

    //TODO simple example, build here JSON object representing graph
    ObjectNode parseToJson(Set<Street> streetNameToNodes) {
        //TODO implement when graph in GraphService.createGraph is ready
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        streetNameToNodes.forEach((street) -> objectNode.put(street.getName(), street.getNodesIds().toString()));
        return objectNode;
    }

}
