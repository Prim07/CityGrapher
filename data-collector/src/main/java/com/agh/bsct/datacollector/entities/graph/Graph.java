package com.agh.bsct.datacollector.entities.graph;

import com.agh.bsct.datacollector.entities.graphdata.Crossing;
import com.agh.bsct.datacollector.entities.graphdata.GraphData;

import java.util.*;

public class Graph {

    private Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap = new HashMap<>();

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
    }

    public Map<GraphNode, List<GraphEdge>> getNodeToEdgesIncidenceMap() {
        return nodeToEdgesIncidenceMap;
    }

    void setNodeToEdgesIncidenceMap(Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap) {
        this.nodeToEdgesIncidenceMap = nodeToEdgesIncidenceMap;
    }

    List<GraphNode> findBiggestConnectedComponent() {
        var graphNodesSet = nodeToEdgesIncidenceMap.keySet();
        var graphNodesList = new ArrayList<>(graphNodesSet);

        var graphNodesSize = graphNodesList.size();

        var nodesComponentIds = new Integer[graphNodesSize];
        for (int i = 0; i < graphNodesSize; i++) {
            nodesComponentIds[i] = 0;
        }

        var currentComponentId = 0;

        var currentComponentNodesIds = new Stack<Integer>();

        for (Integer i = 0; i < graphNodesSize; i++) {
            if (nodesComponentIds[i] > 0) {
                continue;
            }

            currentComponentId++;
            currentComponentNodesIds.push(i);
            nodesComponentIds[i] = currentComponentId;

            while (!currentComponentNodesIds.empty()) {
                var nodeIdFromPeek = currentComponentNodesIds.peek();
                currentComponentNodesIds.pop();

                var graphNodeFromPeek = graphNodesList.get(nodeIdFromPeek);

                for (GraphEdge neighbourEdge : nodeToEdgesIncidenceMap.get(graphNodeFromPeek)) {
                    var neighbour = neighbourEdge.getEndGraphNode();
                    var neighbourId = graphNodesList.indexOf(neighbour);

                    if (nodesComponentIds[neighbourId] > 0) {
                        continue;
                    }

                    currentComponentNodesIds.push(neighbourId);
                    nodesComponentIds[neighbourId] = currentComponentId;
                }
            }
        }

        int biggestCCId = getBiggestCCId(graphNodesSize, nodesComponentIds, currentComponentId);
        return getBiggestCCGraphNodes(graphNodesList, graphNodesSize, nodesComponentIds, biggestCCId);

    }

    private ArrayList<GraphNode> getBiggestCCGraphNodes(ArrayList<GraphNode> graphNodesList, int graphNodesSize,
                                                        Integer[] nodesComponentIds, int biggestConnectedComponentId) {
        var graphNodesFromBiggestCC = new ArrayList<GraphNode>();
        for (int j = 0; j < graphNodesSize; j++) {
            if (nodesComponentIds[j] == biggestConnectedComponentId) {
                graphNodesFromBiggestCC.add(graphNodesList.get(j));
            }
        }
        return graphNodesFromBiggestCC;
    }

    private int getBiggestCCId(int graphNodesSize, Integer[] nodesComponentIds, int currentComponentId) {
        var biggestConnectedComponentId = 0;
        var biggestConnectedComponentSize = 0;
        for (int i = 1; i <= currentComponentId; i++) {
            var connectedComponentSize = 0;
            for (int j = 0; j < graphNodesSize; j++) {
                if (nodesComponentIds[j] == i) {
                    connectedComponentSize++;
                }
            }
            if (connectedComponentSize > biggestConnectedComponentSize) {
                biggestConnectedComponentId = i;
                biggestConnectedComponentSize = connectedComponentSize;
            }
        }
        return biggestConnectedComponentId;
    }

    private void replaceGraphWithItsBiggestConnectedComponent() {
        var graphNodesFromConnectedComponent = findBiggestConnectedComponent();

        var nodeToEdgesIncidenceMapCopy = new HashMap<GraphNode, List<GraphEdge>>();

        for (Map.Entry<GraphNode, List<GraphEdge>> entry : nodeToEdgesIncidenceMap.entrySet()) {
            var graphEdgesList = entry.getValue();
            graphEdgesList.removeIf(graphEdge -> shouldGraphEdgeBeDeleted(graphNodesFromConnectedComponent, graphEdge));

            var graphNode = entry.getKey();
            if (shouldGraphNodeBeKept(graphNodesFromConnectedComponent, graphEdgesList, graphNode)) {
                nodeToEdgesIncidenceMapCopy.put(graphNode, graphEdgesList);
            }
        }

        nodeToEdgesIncidenceMap = nodeToEdgesIncidenceMapCopy;
    }

    private boolean shouldGraphEdgeBeDeleted(List<GraphNode> graphNodesFromConnectedComponent, GraphEdge graphEdge) {
        return !graphNodesFromConnectedComponent.contains(graphEdge.getEndGraphNode());
    }

    private boolean shouldGraphNodeBeKept(List<GraphNode> graphNodesFromConnectedComponent,
                                          List<GraphEdge> graphEdgesList, GraphNode graphNode) {
        return graphNodesFromConnectedComponent.contains(graphNode) && !graphEdgesList.isEmpty();
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
