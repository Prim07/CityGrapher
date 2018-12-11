package com.agh.bsct.datacollector.services.parser;

import com.agh.bsct.api.entities.citydata.CityDataDTO;
import com.agh.bsct.api.entities.citydata.StreetDTO;
import com.agh.bsct.api.entities.graph.Graph;
import com.agh.bsct.api.entities.graph.GraphEdge;
import com.agh.bsct.api.entities.graph.GraphNode;
import com.agh.bsct.api.entities.graphdata.EdgeDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import com.agh.bsct.api.entities.graphdata.NodeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataParser {

    private static final String CROSSINGS_KEY = "crossing";
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

    public ObjectNode parseToJson(CityDataDTO cityData) {
        return parseToJson(cityData, new ArrayList<>());
    }

    private ObjectNode parseToJson(CityDataDTO cityData, List<com.agh.bsct.api.entities.citydata.NodeDTO> hospitals) {
        ArrayList<ObjectNode> jsonStreets = getStreetsParsedToObjectNodes(cityData, hospitals);
        return boxObjectNodesWithName(jsonStreets, WAYS_KEY);
    }

    public ObjectNode parseToJson(GraphDataDTO graphData) {
        return parseToJson(graphData, new ArrayList<>());
    }

    private ObjectNode parseToJson(GraphDataDTO graphData, List<com.agh.bsct.api.entities.citydata.NodeDTO> hospitals) {
        ArrayList<ObjectNode> jsonStreets = getEdgesParsedToObjectNodes(graphData, hospitals);
        return boxObjectNodesWithName(jsonStreets, EDGES_KEY);
    }

    public ObjectNode parseToJson(Graph graph) {
        ArrayList<ObjectNode> jsonIncidenceMapElements = getIncidenceMapParsedToObjectNodes(graph);
        return boxObjectNodesWithName(jsonIncidenceMapElements, GRAPH_KEY);
    }

    private ArrayList<ObjectNode> getStreetsParsedToObjectNodes(CityDataDTO cityData, List<com.agh.bsct.api.entities.citydata.NodeDTO> hospitals) {
        var jsonStreets = new ArrayList<ObjectNode>();
        List<StreetDTO> streets = cityData.getStreets();
        for (StreetDTO street : streets) {
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

    private ArrayList<ObjectNode> getEdgesParsedToObjectNodes(GraphDataDTO graphData, List<com.agh.bsct.api.entities.citydata.NodeDTO> hospitals) {
        var jsonStreets = new ArrayList<ObjectNode>();
        List<EdgeDTO> edgeDTOS = graphData.getEdgeDTOS();
        for (EdgeDTO edgeDTO : edgeDTOS) {
            ObjectNode jsonStreet = objectMapper.createObjectNode();
            jsonStreet.put(ID_KEY, edgeDTOS.indexOf(edgeDTO));
            jsonStreet.put(WEIGHT_KEY, edgeDTO.getWeight());
            var streetNodesIds = edgeDTO.getStreetDTO().getNodesIds();
            var crossingDTOS = graphData.getNodeDTOS();
            ArrayList<ObjectNode> jsonNodes = getCrossingsParsedToObjectNodes(streetNodesIds, crossingDTOS, hospitals);
            jsonStreet.putArray(CROSSINGS_KEY).addAll(jsonNodes);
            jsonStreets.add(jsonStreet);
        }
        return jsonStreets;
    }

    private ArrayList<ObjectNode> getNodesParsedToObjectNodes(List<Long> streetNodesIds,
                                                              List<com.agh.bsct.api.entities.citydata.NodeDTO> nodes,
                                                              List<com.agh.bsct.api.entities.citydata.NodeDTO> hospitals) {
        ArrayList<ObjectNode> jsonNodes = new ArrayList<>();
        for (Long nodeId : streetNodesIds) {
            ObjectNode jsonNode = objectMapper.createObjectNode();
            com.agh.bsct.api.entities.citydata.NodeDTO node = getNodeWithGivenId(nodeId, nodes);
            jsonNode.put(ID_KEY, node.getId());
            jsonNode.put(LATITUDE_KEY, node.getLat());
            jsonNode.put(LONGITUDE_KEY, node.getLon());
            jsonNode.put(CROSSINGS_KEY, node.isCrossing());
            jsonNode.put(HOSPITAL_KEY, hospitals.contains(node));
            jsonNodes.add(jsonNode);
        }
        return jsonNodes;
    }

    private ArrayList<ObjectNode> getCrossingsParsedToObjectNodes(List<Long> streetNodesIds,
                                                                  List<NodeDTO> nodeDTOS,
                                                                  List<com.agh.bsct.api.entities.citydata.NodeDTO> hospitals) {
        ArrayList<ObjectNode> jsonNodes = new ArrayList<>();
        for (Long nodeId : streetNodesIds) {
            ObjectNode jsonNode = objectMapper.createObjectNode();
            NodeDTO crossing = getCrossingWithGivenId(nodeId, nodeDTOS);
            jsonNode.put(ID_KEY, crossing.getNodeDTO().getId());
            jsonNode.put(WEIGHT_KEY, crossing.getWeight());
            jsonNode.put(LATITUDE_KEY, crossing.getNodeDTO().getLat());
            jsonNode.put(LONGITUDE_KEY, crossing.getNodeDTO().getLon());
            jsonNode.put(CROSSINGS_KEY, crossing.getNodeDTO().isCrossing());
            jsonNode.put(HOSPITAL_KEY, hospitals.contains(crossing.getNodeDTO()));
            jsonNodes.add(jsonNode);
        }
        return jsonNodes;
    }

    private NodeDTO getCrossingWithGivenId(Long nodeId, List<NodeDTO> nodeDTOS) {
        return nodeDTOS.stream()
                .filter(nodeDTO -> nodeDTO.getNodeDTO().getId().equals(nodeId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Cannot find GraphNode with given id"));
    }


    private com.agh.bsct.api.entities.citydata.NodeDTO getNodeWithGivenId(Long nodeId, List<com.agh.bsct.api.entities.citydata.NodeDTO> nodes) {
        return nodes.stream()
                .filter(node -> node.getId().equals(nodeId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Cannot find GraphNode with given id"));
    }

    private void boxObjectNodesIntoEdgesArray(ObjectNode jsonBase, ArrayList<ObjectNode> jsonObjects) {
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
            boxObjectNodesIntoEdgesArray(jsonIncidenceMapElement, jsonEdges);
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
