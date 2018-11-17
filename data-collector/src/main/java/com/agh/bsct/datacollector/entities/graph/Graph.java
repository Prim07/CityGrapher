package com.agh.bsct.datacollector.entities.graph;

import com.agh.bsct.datacollector.entities.graphdata.Crossing;
import com.agh.bsct.datacollector.entities.graphdata.GraphData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {

    private Map<Node, List<Edge>> incidenceMap = new HashMap<>();

    public Graph(GraphData graphData) {
        var edges = graphData.getEdges();
        var crossing = graphData.getCrossings();

        for(com.agh.bsct.datacollector.entities.graphdata.Edge edge : edges) {
            var edgeNodeIds = edge.getStreet().getNodesIds();
            var edgeWeight = edge.getWeight();

            var firstNodeId = edgeNodeIds.get(0);
            var firstNode = getNodeWithGivenId(firstNodeId);
            if (firstNode == null) {
                var firstCrossing = getCrossingWithGivenId(crossing, firstNodeId);
                firstNode = new Node(firstNodeId, firstCrossing.getWeight());
            }

            var lastNodeId = edgeNodeIds.get(edgeNodeIds.size()-1);
            var lastNode = getNodeWithGivenId(lastNodeId);
            if (lastNode == null) {
                var lastCrossing = getCrossingWithGivenId(crossing, lastNodeId);
                lastNode = new Node(lastNodeId, lastCrossing.getWeight());
            }

            var firstNodeEdges = incidenceMap.computeIfAbsent(firstNode, k -> new ArrayList<>());
            firstNodeEdges.add(new Edge(lastNode, edgeWeight));

            var lastNodeEdges = incidenceMap.computeIfAbsent(lastNode, k -> new ArrayList<>());
            lastNodeEdges.add(new Edge(firstNode, edgeWeight));
        }
    }

    public Map<Node, List<Edge>> getIncidenceMap() {
        return incidenceMap;
    }

    private Node getNodeWithGivenId(Long nodeId) {
        return incidenceMap.keySet().stream()
                .filter(node -> node.getId().equals(nodeId))
                .findAny()
                .orElse(null);
    }

    private Crossing getCrossingWithGivenId(List<Crossing> crossings, Long nodeId) {
        return crossings.stream()
                .filter(crossing -> crossing.getNode().getId().equals(nodeId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Cannot find Node with given id: " + nodeId));
    }

}
