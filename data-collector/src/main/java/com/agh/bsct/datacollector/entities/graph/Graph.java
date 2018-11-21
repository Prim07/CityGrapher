package com.agh.bsct.datacollector.entities.graph;

import com.agh.bsct.datacollector.entities.graphdata.Crossing;
import com.agh.bsct.datacollector.entities.graphdata.GraphData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {

    private Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap = new HashMap<>();
    private List<GraphNode> graphNodes = new ArrayList<>();

    private double[][] shortestPathsDistances;

    public Graph(GraphData graphData) {
        var edges = graphData.getEdges();
        var crossings = graphData.getCrossings();

        for (var edge : edges) {
            var edgeNodeIds = edge.getStreet().getNodesIds();
            var edgeWeight = edge.getWeight();

            var firstNodeId = edgeNodeIds.get(0);
            var firstNode = getNodeForId(firstNodeId);
            if (firstNode == null) {
                var firstCrossing = getCrossingWithId(crossings, firstNodeId);
                firstNode = new GraphNode(firstNodeId, firstCrossing.getWeight());
            }

            var lastNodeId = edgeNodeIds.get(edgeNodeIds.size() - 1);
            var lastNode = getNodeForId(lastNodeId);
            if (lastNode == null) {
                var lastCrossing = getCrossingWithId(crossings, lastNodeId);
                lastNode = new GraphNode(lastNodeId, lastCrossing.getWeight());
            }

            graphNodes.add(firstNode);
            graphNodes.add(lastNode);

            var firstNodeEdges = nodeToEdgesIncidenceMap.computeIfAbsent(firstNode, node -> new ArrayList<>());
            firstNodeEdges.add(new GraphEdge(lastNode, edgeWeight));

            var lastNodeEdges = nodeToEdgesIncidenceMap.computeIfAbsent(lastNode, node -> new ArrayList<>());
            lastNodeEdges.add(new GraphEdge(firstNode, edgeWeight));
        }
    }

    public Map<GraphNode, List<GraphEdge>> getNodeToEdgesIncidenceMap() {
        return nodeToEdgesIncidenceMap;
    }

    private GraphNode getNodeForId(Long nodeId) {
        return nodeToEdgesIncidenceMap.keySet().stream()
                .filter(graphNode -> graphNode.getId().equals(nodeId))
                .findAny()
                .orElse(null);
    }

    private Crossing getCrossingWithId(List<Crossing> crossings, Long nodeId) {
        return crossings.stream()
                .filter(crossing -> crossing.getNode().getId().equals(nodeId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Cannot find Crossing for GraphNode with given id: "
                        + nodeId));
    }

    public void calculateShortestPathsDistances() {
        int graphNodesCount = nodeToEdgesIncidenceMap.size();
        shortestPathsDistances = new double[graphNodesCount][graphNodesCount];

        //initialize distances with edge weights or infinity when edge doesn't exist
        for (int i = 0; i < graphNodesCount; i++) {
            for (int j = 0; j < graphNodesCount; j++) {
                if (i == j) {
                    shortestPathsDistances[i][j] = 0;
                } else {
                    double edgeWeight = getEdgeWeight(graphNodes.get(i), graphNodes.get(j));
                    if (edgeWeight > 0) {
                        shortestPathsDistances[i][j] = edgeWeight;
                    } else {
                        shortestPathsDistances[i][j] = Double.MAX_VALUE;
                    }
                }
            }
        }

        //find shortest paths distances
        for (int k = 0; k < graphNodesCount; k++) {
            for (int i = 0; i < graphNodesCount; i++) {
                for (int j = 0; j < graphNodesCount; j++) {
                    if (shortestPathsDistances[i][j] > shortestPathsDistances[i][k] + shortestPathsDistances[k][j]) {
                        shortestPathsDistances[i][j] = shortestPathsDistances[i][k] + shortestPathsDistances[k][j];
                    }
                }
            }
        }

        //TODO if two nodes don't have a connection, then distance is infinity
        //it will generate problems while calculating distances to hospital
        //maybe it should be 0


    }

    private double getEdgeWeight(GraphNode graphNode1, GraphNode graphNode2) {
        GraphEdge graphEdgeToFind = nodeToEdgesIncidenceMap.get(graphNode1).stream()
                .filter(graphEdge -> graphEdge.getEndGraphNode().equals(graphNode2))
                .findFirst()
                .orElse(null);
        return graphEdgeToFind != null ? graphEdgeToFind.getWeight() : -1;
    }

}