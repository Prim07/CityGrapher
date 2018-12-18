package com.agh.bsct.datacollector.services.parser;

import com.agh.bsct.api.entities.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.entities.graphdata.EdgeDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import com.agh.bsct.api.entities.graphdata.NodeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataParser {

    private static final String CROSSINGS_KEY = "crossings";
    private static final String EDGES_KEY = "edges";
    private static final String HOSPITAL_KEY = "hospital";
    private static final String ID_KEY = "id";
    private static final String LATITUDE_KEY = "lat";
    private static final String LONGITUDE_KEY = "lon";
    private static final String WEIGHT_KEY = "weight";

    private ObjectMapper objectMapper = new ObjectMapper();

    public ObjectNode parseToJson(GraphDataDTO graphData, List<GeographicalNodeDTO> hospitals) {
        ArrayList<ObjectNode> jsonStreets = getEdgesParsedToObjectNodes(graphData, hospitals);
        return boxObjectNodesWithName(jsonStreets, EDGES_KEY);
    }

    /* TODO to remove after implementing calling algorithm from UI and retrieving real data
    public ObjectNode parseToJson(Graph graph) {
        ArrayList<ObjectNode> jsonIncidenceMapElements = getIncidenceMapParsedToObjectNodes(graph);
        return boxObjectNodesWithName(jsonIncidenceMapElements, GRAPH_KEY);
    }*/

    private ArrayList<ObjectNode> getEdgesParsedToObjectNodes(GraphDataDTO graphDataDTO,
                                                              List<GeographicalNodeDTO> hospitals) {
        var jsonStreets = new ArrayList<ObjectNode>();
        List<EdgeDTO> edgeDTOS = graphDataDTO.getEdgeDTOS();
        for (EdgeDTO edgeDTO : edgeDTOS) {
            ObjectNode jsonStreet = objectMapper.createObjectNode();
            jsonStreet.put(ID_KEY, edgeDTOS.indexOf(edgeDTO));
            jsonStreet.put(WEIGHT_KEY, edgeDTO.getWeight());
            var streetNodesIds = edgeDTO.getStreetDTO().getNodesIds();
            var crossingDTOS = graphDataDTO.getNodeDTOS();
            ArrayList<ObjectNode> jsonNodes = getCrossingsParsedToObjectNodes(streetNodesIds, crossingDTOS, hospitals);
            jsonStreet.putArray(CROSSINGS_KEY).addAll(jsonNodes);
            jsonStreets.add(jsonStreet);
        }
        return jsonStreets;
    }

    private ArrayList<ObjectNode> getCrossingsParsedToObjectNodes(List<Long> streetNodesIds,
                                                                  List<NodeDTO> nodeDTOS,
                                                                  List<GeographicalNodeDTO> hospitals) {
        ArrayList<ObjectNode> jsonNodes = new ArrayList<>();
        for (Long nodeId : streetNodesIds) {
            ObjectNode jsonNode = objectMapper.createObjectNode();
            NodeDTO crossing = getCrossingWithGivenId(nodeId, nodeDTOS);
            jsonNode.put(ID_KEY, crossing.getGeographicalNodeDTO().getId());
            jsonNode.put(WEIGHT_KEY, crossing.getWeight());
            jsonNode.put(LATITUDE_KEY, crossing.getGeographicalNodeDTO().getLat());
            jsonNode.put(LONGITUDE_KEY, crossing.getGeographicalNodeDTO().getLon());
            jsonNode.put(CROSSINGS_KEY, crossing.getGeographicalNodeDTO().isCrossing());
            jsonNode.put(HOSPITAL_KEY, hospitals.contains(crossing.getGeographicalNodeDTO()));
            jsonNodes.add(jsonNode);
        }
        return jsonNodes;
    }

    private NodeDTO getCrossingWithGivenId(Long nodeId, List<NodeDTO> nodeDTOS) {
        return nodeDTOS.stream()
                .filter(nodeDTO -> nodeDTO.getGeographicalNodeDTO().getId().equals(nodeId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Cannot find GraphNode with given taskId"));
    }

    private ObjectNode boxObjectNodesWithName(ArrayList<ObjectNode> jsonObjects, String name) {
        ArrayNode jsonObjectsArrayNode = objectMapper.valueToTree(jsonObjects);
        ObjectNode jsonBase = objectMapper.createObjectNode();
        jsonBase.putArray(name).addAll(jsonObjectsArrayNode);

        return jsonBase;
    }

    /* TODO to remove after implementing calling algorithm from UI and retrieving real data
    private ArrayList<ObjectNode> getIncidenceMapParsedToObjectNodes(Graph graph) {
        var incidenceMap = graph.getNodeToEdgesIncidenceMap();
        ArrayList<ObjectNode> jsonIncidenceMapElements = new ArrayList<>();

        for (Map.Entry<GraphNode, List<GraphEdge>> entry : incidenceMap.entrySet()) {
            var startNode = entry.getKey();
            var jsonStartNode = objectMapper.createObjectNode();
            jsonStartNode.put(ID_KEY, startNode.getTaskId());
            jsonStartNode.put(WEIGHT_KEY, startNode.getWeight());

            ArrayList<ObjectNode> jsonEdges = getEdgesParsedToObjectNodes(entry);

            var jsonIncidenceMapElement = objectMapper.createObjectNode();
            jsonIncidenceMapElement.putPOJO(NODE_KEY, jsonStartNode);
            boxObjectNodesIntoEdgesArray(jsonIncidenceMapElement, jsonEdges);
            jsonIncidenceMapElements.add(jsonIncidenceMapElement);
        }

        return jsonIncidenceMapElements;
    }*/

    /* TODO to remove after implementing calling algorithm from UI and retrieving real data
    private ArrayList<ObjectNode> getEdgesParsedToObjectNodes(Map.Entry<GraphNode, List<GraphEdge>> entry) {
        var edges = entry.getValue();
        var jsonEdges = new ArrayList<ObjectNode>();

        for (GraphEdge graphEdge : edges) {
            var endNode = graphEdge.getEndGraphNode();
            var jsonEndNode = objectMapper.createObjectNode();
            jsonEndNode.put(ID_KEY, endNode.getTaskId());
            jsonEndNode.put(WEIGHT_KEY, endNode.getWeight());

            var jsonEdge = objectMapper.createObjectNode();
            jsonEdge.putPOJO(NODE_KEY, jsonEndNode);
            jsonEdge.put(WEIGHT_KEY, graphEdge.getWeight());
            jsonEdges.add(jsonEdge);
        }

        return jsonEdges;
    }*/
}
