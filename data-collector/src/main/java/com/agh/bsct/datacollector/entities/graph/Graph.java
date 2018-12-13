package com.agh.bsct.datacollector.entities.graph;

import com.agh.bsct.datacollector.entities.graphdata.Crossing;
import com.agh.bsct.datacollector.entities.graphdata.GraphData;

import java.util.*;

public class Graph {

    private Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap = new HashMap<>();

    private Double[][] shortestPathsDistances;

    Graph() {
    }

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

            var firstNodeEdges = nodeToEdgesIncidenceMap.computeIfAbsent(firstNode, node -> new ArrayList<>());
            firstNodeEdges.add(new GraphEdge(lastNode, edgeWeight));

            var lastNodeEdges = nodeToEdgesIncidenceMap.computeIfAbsent(lastNode, node -> new ArrayList<>());
            lastNodeEdges.add(new GraphEdge(firstNode, edgeWeight));
        }

        replaceGraphWithItsBiggestConnectedComponent();

        calculateShortestPathsDistances();
    }

    public Map<GraphNode, List<GraphEdge>> getNodeToEdgesIncidenceMap() {
        return nodeToEdgesIncidenceMap;
    }

    Double[][] getShortestPathsDistances() {
        return shortestPathsDistances;
    }

    void setNodeToEdgesIncidenceMap(Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap) {
        this.nodeToEdgesIncidenceMap = nodeToEdgesIncidenceMap;
    }

    void calculateShortestPathsDistances() {
        var graphNodes = new ArrayList<>(nodeToEdgesIncidenceMap.keySet());

        int graphNodesCount = nodeToEdgesIncidenceMap.size();
        shortestPathsDistances = new Double[graphNodesCount][graphNodesCount];

        //initialize distances with edge weights or infinity when edge doesn't exist
        for (int i = 0; i < graphNodesCount; i++) {
            for (int j = 0; j < graphNodesCount; j++) {
                if (i == j) {
                    shortestPathsDistances[i][j] = 0.0;
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

    }

    List<GraphNode> getBiggestConnectedComponent() {
        var graphNodesSet = nodeToEdgesIncidenceMap.keySet();
        var graphNodesList = new ArrayList<>(graphNodesSet);

        var graphNodesSize = graphNodesList.size();

        var commonComponentsSizes = new Integer[graphNodesSize];
        for (int i = 0; i < graphNodesSize; i++) {
            commonComponentsSizes[i] = 0;
        }

        var numberOfCommonComponents = 0;

        var stack = new Stack<Integer>();

        for (Integer i = 0; i < graphNodesSize; i++) {
            if (commonComponentsSizes[i] > 0) {
                continue;
            }

            numberOfCommonComponents++;
            stack.push(i);
            commonComponentsSizes[i] = numberOfCommonComponents;

            while (!stack.empty()) {
                var nodeIdFromPeek = stack.peek();
                stack.pop();

                var graphNodeFromPeek = graphNodesList.get(nodeIdFromPeek);

                for (GraphEdge neighbourEdge : nodeToEdgesIncidenceMap.get(graphNodeFromPeek)) {
                    var neighbour = neighbourEdge.getEndGraphNode();
                    var neighbourId = graphNodesList.indexOf(neighbour);

                    if (commonComponentsSizes[neighbourId] > 0) {
                        continue;
                    }

                    stack.push(neighbourId);
                    commonComponentsSizes[neighbourId] = numberOfCommonComponents;
                }
            }
        }

        var biggestCommonComponentId = 0;
        var biggestCommonComponentSize = 0;
        for (int i = 1; i <= numberOfCommonComponents; i++) {
            var commonComponentSize = 0;
            for (int j = 0; j < graphNodesSize; j++) {
                if (commonComponentsSizes[j] == i) {
                    commonComponentSize++;
                }
            }
            if (commonComponentSize > biggestCommonComponentSize) {
                biggestCommonComponentId = i;
                biggestCommonComponentSize = commonComponentSize;
            }
        }

        var graphNodesFromBiggestCC = new ArrayList<GraphNode>();
        for (int j = 0; j < graphNodesSize; j++) {
            if (commonComponentsSizes[j] == biggestCommonComponentId) {
                graphNodesFromBiggestCC.add(graphNodesList.get(j));
            }
        }

        return graphNodesFromBiggestCC;

    }

    private void replaceGraphWithItsBiggestConnectedComponent() {
        var graphNodesFromCommonComponent = getBiggestConnectedComponent();

        var nodeToEdgesIncidenceMapCopy = new HashMap<GraphNode, List<GraphEdge>>();

        for (Map.Entry<GraphNode, List<GraphEdge>> entry : nodeToEdgesIncidenceMap.entrySet()) {
            var graphEdgesList = entry.getValue();
            graphEdgesList.removeIf(graphEdge -> shouldGraphEdgeBeDeleted(graphNodesFromCommonComponent, graphEdge));

            var graphNode = entry.getKey();
            if (shouldGraphNodeBeKept(graphNodesFromCommonComponent, graphEdgesList, graphNode)) {
                nodeToEdgesIncidenceMapCopy.put(graphNode, graphEdgesList);
            }
        }

        nodeToEdgesIncidenceMap = nodeToEdgesIncidenceMapCopy;
    }

    private boolean shouldGraphEdgeBeDeleted(List<GraphNode> graphNodesFromCommonComponent, GraphEdge graphEdge) {
        return !graphNodesFromCommonComponent.contains(graphEdge.getEndGraphNode());
    }

    private boolean shouldGraphNodeBeKept(List<GraphNode> graphNodesFromCommonComponent, List<GraphEdge> graphEdgesList, GraphNode graphNode) {
        return graphNodesFromCommonComponent.contains(graphNode) && !graphEdgesList.isEmpty();
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

    private double getEdgeWeight(GraphNode graphNode1, GraphNode graphNode2) {
        GraphEdge graphEdgeToFind = nodeToEdgesIncidenceMap.get(graphNode1).stream()
                .filter(graphEdge -> graphEdge.getEndGraphNode().equals(graphNode2))
                .findFirst()
                .orElse(null);
        return graphEdgeToFind != null ? graphEdgeToFind.getWeight() : -1;
    }

}
