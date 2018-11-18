package com.agh.bsct.datacollector.services.parser;

import com.agh.bsct.datacollector.entities.citydata.CityData;
import com.agh.bsct.datacollector.entities.citydata.Node;
import com.agh.bsct.datacollector.entities.citydata.Street;
import com.agh.bsct.datacollector.entities.graph.Graph;
import com.agh.bsct.datacollector.entities.graph.GraphEdge;
import com.agh.bsct.datacollector.entities.graph.GraphNode;
import com.agh.bsct.datacollector.entities.graphdata.GraphData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataParser {

    private static final String JUNCTION_KEY = "crossing";
    private static final String EDGES_KEY = "edges";
    private static final String GRAPH_KEY = "graph";
    private static final String HOSPITAL_KEY = "hospital";
    private static final String ID_KEY = "id";
    private static final String LATITUDE_KEY = "lat";
    private static final String LONGITUDE_KEY = "lon";
    private static final String NODE_KEY = "node";
    private static final String NODES_KEY = "nodes";
    private static final String WAYS_KEY = "ways";
    private static final String WEIGHT_KEY = "weight";

    private ObjectMapper objectMapper = new ObjectMapper();

    public ObjectNode parseToJson(GraphData graphData, List<Node> hospitals) {
        return parseToJson(graphData.toCityData(), hospitals);
    }

    public ObjectNode parseToJson(CityData cityData) {
        return parseToJson(cityData, new ArrayList<>());
    }

    public ObjectNode parseToJson(Graph graph) {
        ArrayList<ObjectNode> jsonIncidenceMapElements = getIncidenceMapParsedToObjectNodes(graph);
        return boxObjectNodesWithName(jsonIncidenceMapElements, GRAPH_KEY);
    }

    private ObjectNode parseToJson(CityData cityData, List<Node> hospitals) {
        ArrayList<ObjectNode> jsonStreets = getStreetsParsedToObjectNodes(cityData, hospitals);
        return boxObjectNodesWithName(jsonStreets, WAYS_KEY);
    }

    private ArrayList<ObjectNode> getStreetsParsedToObjectNodes(CityData cityData, List<Node> hospitals) {
        var jsonStreets = new ArrayList<ObjectNode>();
        List<Street> streets = cityData.getStreets();
        for (Street street : streets) {
            ObjectNode jsonStreet = objectMapper.createObjectNode();
            jsonStreet.put(ID_KEY, streets.indexOf(street));
            var streetNodesIds = street.getNodesIds();
            var nodes = cityData.getNodes();
            ArrayList<ObjectNode> jsonNodes = getNodesParsedToObjectNodes(streetNodesIds, nodes, hospitals);
            jsonStreet.putArray(NODES_KEY).addAll(jsonNodes);
            jsonStreets.add(jsonStreet);
        }
        return jsonStreets;
    }

    private ArrayList<ObjectNode> getNodesParsedToObjectNodes(List<Long> streetNodesIds,
                                                              List<Node> nodes,
                                                              List<Node> hospitals) {
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
                .orElseThrow(() -> new IllegalStateException("Cannot find GraphNode with given id"));
    }

    private void boxObjectNodesWithName(ObjectNode jsonBase, ArrayList<ObjectNode> jsonObjects) {
        ArrayNode jsonObjectsArrayNode = objectMapper.valueToTree(jsonObjects);
        jsonBase.putArray(DataParser.EDGES_KEY).addAll(jsonObjectsArrayNode);
    }

    private ObjectNode boxObjectNodesWithName(ArrayList<ObjectNode> jsonObjects, String name) {
        ArrayNode jsonObjectsArrayNode = objectMapper.valueToTree(jsonObjects);
        ObjectNode jsonBase = objectMapper.createObjectNode();
        jsonBase.putArray(name).addAll(jsonObjectsArrayNode);

        return jsonBase;
    }

    private ArrayList<ObjectNode> getIncidenceMapParsedToObjectNodes(Graph graph) {
        var incidenceMap = graph.getNodeToEdgesIncidenceMap();

        ArrayList<ObjectNode> jsonIncidenceMapElements = new ArrayList<>();

        for (Map.Entry<GraphNode, List<GraphEdge>> entry : incidenceMap.entrySet()) {
            var startNode = entry.getKey();
            var jsonStartNode = objectMapper.createObjectNode();
            jsonStartNode.put(ID_KEY, startNode.getId());
            jsonStartNode.put(WEIGHT_KEY, startNode.getWeight());

            ArrayList<ObjectNode> jsonEdges = getEdgesParsedToObjectNodes(entry);

            var jsonIncidenceMapElement = objectMapper.createObjectNode();
            jsonIncidenceMapElement.putPOJO(NODE_KEY, jsonStartNode);
            boxObjectNodesWithName(jsonIncidenceMapElement, jsonEdges);
            jsonIncidenceMapElements.add(jsonIncidenceMapElement);
        }
        return jsonIncidenceMapElements;
    }

    private ArrayList<ObjectNode> getEdgesParsedToObjectNodes(Map.Entry<GraphNode, List<GraphEdge>> entry) {
        var edges = entry.getValue();
        var jsonEdges = new ArrayList<ObjectNode>();

        for (GraphEdge graphEdge : edges) {
            var endNode = graphEdge.getEndGraphNode();
            var jsonEndNode = objectMapper.createObjectNode();
            jsonEndNode.put(ID_KEY, endNode.getId());
            jsonEndNode.put(WEIGHT_KEY, endNode.getWeight());

            var jsonEdge = objectMapper.createObjectNode();
            jsonEdge.putPOJO(NODE_KEY, jsonEndNode);
            jsonEdge.put(WEIGHT_KEY, graphEdge.getWeight());
            jsonEdges.add(jsonEdge);
        }

        return jsonEdges;
    }
}
