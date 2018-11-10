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

    private static final String ID_KEY = "id";
    private static final String NODES_KEY = "nodes";
    private static final String LATITUDE_KEY = "lat";
    private static final String LONGITUDE_KEY = "lon";
    private static final String JUNCTION_KEY = "junction";
    private static final String HOSPITAL_KEY = "hospital";
    private static final String WAYS_KEY = "ways";

    //TODO simple example, build here JSON object representing parser
    public ObjectNode parseToJson(GraphData graphData) {
        //TODO implement when parser in GraphService.returnGraphAsObjectNode is ready
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        for (var edge : graphData.getEdges()) {
            Street street = edge.getStreet();
            objectNode.put(street.getName(), street.getNodesIds().toString());

        }
        return objectNode;
    }

    public ObjectNode parseToJson(CityData cityData) {
        var objectMapper = new ObjectMapper();
        ArrayList<ObjectNode> jsonStreets = addStreets(cityData, objectMapper);
        ArrayList<ObjectNode> jsonNodes = addNodes(cityData, objectMapper);
        return mapToObjectNode(objectMapper, jsonStreets, jsonNodes);
    }

    private ArrayList<ObjectNode> addStreets(CityData cityData, ObjectMapper objectMapper) {
        var jsonStreets = new ArrayList<ObjectNode>();
        List<Street> streets = cityData.getStreets();
        for (Street street : streets) {
            ObjectNode jsonStreet = objectMapper.createObjectNode();
            jsonStreet.put(ID_KEY, streets.indexOf(street));
            ArrayNode jsonNodeIds = objectMapper.valueToTree(street.getNodesIds());
            jsonStreet.putArray(NODES_KEY).addAll(jsonNodeIds);
            jsonStreets.add(jsonStreet);
        }
        return jsonStreets;
    }

    private ArrayList<ObjectNode> addNodes(CityData cityData, ObjectMapper objectMapper) {
        ArrayList<ObjectNode> jsonNodes = new ArrayList<>();
        List<Node> nodes = cityData.getNodes();
        List<Node> crossing = cityData.getCrossings();
        for (Node node : nodes) {
            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put(ID_KEY, node.getId());
            jsonNode.put(LATITUDE_KEY, node.getLat());
            jsonNode.put(LONGITUDE_KEY, node.getLon());
            jsonNode.put(JUNCTION_KEY, crossing.contains(node));
            jsonNode.put(HOSPITAL_KEY, false);
            jsonNodes.add(jsonNode);
        }
        return jsonNodes;
    }

    private ObjectNode mapToObjectNode(ObjectMapper objectMapper, ArrayList<ObjectNode> jsonStreets, ArrayList<ObjectNode> jsonNodes) {
        ObjectNode jsonBase = objectMapper.createObjectNode();

        ArrayNode allJsonStreets = objectMapper.valueToTree(jsonStreets);
        jsonBase.putArray(WAYS_KEY).addAll(allJsonStreets);

        ArrayNode allJsonNodes = objectMapper.valueToTree(jsonNodes);
        jsonBase.putArray(NODES_KEY).addAll(allJsonNodes);

        return jsonBase;
    }

}
