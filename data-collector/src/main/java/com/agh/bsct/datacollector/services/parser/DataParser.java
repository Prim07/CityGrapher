package com.agh.bsct.datacollector.services.parser;

import com.agh.bsct.datacollector.entities.citydata.CityData;
import com.agh.bsct.datacollector.entities.citydata.Node;
import com.agh.bsct.datacollector.entities.citydata.Street;
import com.agh.bsct.datacollector.entities.graphdata.GraphData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public ObjectNode parseToJson(CityData cityData) {
        ObjectMapper objectMapper = new ObjectMapper();

        ArrayList<ObjectNode> jsonStreets = new ArrayList<>();
        List<Street> streets = cityData.getStreets();
        for (Street street : streets) {
            ObjectNode jsonStreet = objectMapper.createObjectNode();
            jsonStreet.put("id", streets.indexOf(street));
            ArrayNode jsonNodeIds = objectMapper.valueToTree(street.getNodesIds());
            jsonStreet.putArray("nodes").addAll(jsonNodeIds);
            jsonStreets.add(jsonStreet);
        }

        ArrayList<ObjectNode> jsonNodes = new ArrayList<>();
        List<Node> nodes = cityData.getNodes();
        List<Node> crossing = cityData.getCrossings();
        for (Node node : nodes) {
            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put("id", node.getId());
            jsonNode.put("lat", node.getLat());
            jsonNode.put("lon", node.getLon());
            jsonNode.put("junction", crossing.contains(node));
            jsonNode.put("hospital", false);
            jsonNodes.add(jsonNode);
        }

        ObjectNode jsonBase = objectMapper.createObjectNode();

        ArrayNode allJsonStreets = objectMapper.valueToTree(jsonStreets);
        jsonBase.putArray("ways").addAll(allJsonStreets);

        ArrayNode allJsonNodes = objectMapper.valueToTree(jsonNodes);
        jsonBase.putArray("nodes").addAll(allJsonNodes);

        return jsonBase;
    }

}
