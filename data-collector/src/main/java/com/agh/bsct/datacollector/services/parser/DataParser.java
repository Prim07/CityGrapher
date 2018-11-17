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
    private static final String JUNCTION_KEY = "crossing";
    private static final String HOSPITAL_KEY = "hospital";
    private static final String WAYS_KEY = "ways";

    public ObjectNode parseToJson(GraphData graphData, List<Node> hospitals) {
        return parseToJson(graphData.toCityData(), hospitals);
    }

    public ObjectNode parseToJson(CityData cityData) {
        return parseToJson(cityData, new ArrayList<>());
    }

    private ObjectNode parseToJson(CityData cityData, List<Node> hospitals) {
        var objectMapper = new ObjectMapper();
        ArrayList<ObjectNode> jsonStreets = addStreets(cityData, hospitals, objectMapper);
        return mapToObjectNode(objectMapper, jsonStreets);
    }

    private ArrayList<ObjectNode> addStreets(CityData cityData, List<Node> hospitals, ObjectMapper objectMapper) {
        var jsonStreets = new ArrayList<ObjectNode>();
        List<Street> streets = cityData.getStreets();
        for (Street street : streets) {
            ObjectNode jsonStreet = objectMapper.createObjectNode();
            jsonStreet.put(ID_KEY, streets.indexOf(street));
            var streetNodesIds = street.getNodesIds();
            var nodes = cityData.getNodes();
            ArrayList<ObjectNode> jsonNodes = addNodes(streetNodesIds, nodes, hospitals, objectMapper);
            jsonStreet.putArray(NODES_KEY).addAll(jsonNodes);
            jsonStreets.add(jsonStreet);
        }
        return jsonStreets;
    }

    private ArrayList<ObjectNode> addNodes(List<Long> streetNodesIds,
                                           List<Node> nodes,
                                           List<Node> hospitals,
                                           ObjectMapper objectMapper) {
        ArrayList<ObjectNode> jsonNodes = new ArrayList<>();
        for (Long nodeId : streetNodesIds) {
            ObjectNode jsonNode = objectMapper.createObjectNode();
            Node node = getNodeWithGivenId(nodeId, nodes);
            jsonNode.put(ID_KEY, node.getId());
            jsonNode.put(LATITUDE_KEY, node.getLat());
            jsonNode.put(LONGITUDE_KEY, node.getLon());
            jsonNode.put(JUNCTION_KEY, node.isCrossing());
            jsonNode.put(HOSPITAL_KEY, hospitals.contains(node));
            jsonNodes.add(jsonNode);
        }
        return jsonNodes;
    }

    private Node getNodeWithGivenId(Long nodeId, List<Node> nodes) {
        return nodes.stream()
                .filter(node -> node.getId().equals(nodeId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Cannot find Node with given id"));
    }

    private ObjectNode mapToObjectNode(ObjectMapper objectMapper, ArrayList<ObjectNode> jsonStreets) {
        ObjectNode jsonBase = objectMapper.createObjectNode();

        ArrayNode allJsonStreets = objectMapper.valueToTree(jsonStreets);
        jsonBase.putArray(WAYS_KEY).addAll(allJsonStreets);

        return jsonBase;
    }

}
