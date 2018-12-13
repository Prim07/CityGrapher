package com.agh.bsct.datacollector.entities.graph;

import com.agh.bsct.datacollector.entities.graphdata.Crossing;
import com.agh.bsct.datacollector.entities.graphdata.GraphData;

import java.util.*;

public class Graph {

    private Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap = new HashMap<>();

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
    }

    // TODO PP przenieść tę metodę w bardziej odpowiednie miejsce i nie zapomnieć jej wywołać
    private void replaceGraphWithItsBiggestCommonComponent() {
        var graphNodesFromCommonComponent = getBiggestCommonComponent();

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

    private List<GraphNode> getBiggestCommonComponent() {
        var graphNodesSet = nodeToEdgesIncidenceMap.keySet();
        var graphNodesList = new ArrayList<>(graphNodesSet);

        var graphNodesSize = graphNodesList.size();

        var commonComponentsSizes = new Integer[graphNodesSize];
        for (int i = 0; i < graphNodesSize; i++) {
            commonComponentsSizes[i] = 0;
        }

        var numberOfCommonComponents = Integer.valueOf(0);

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

}
