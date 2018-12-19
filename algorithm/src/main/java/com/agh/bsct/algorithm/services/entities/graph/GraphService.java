package com.agh.bsct.algorithm.services.entities.graph;

import com.agh.bsct.algorithm.entities.graph.Graph;
import com.agh.bsct.algorithm.entities.graph.GraphEdge;
import com.agh.bsct.algorithm.entities.graph.GraphNode;
import com.agh.bsct.algorithm.services.entities.graphdata.GraphDataService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.api.entities.graphdata.NodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphService {

    private GraphDataService graphDataService;

    @Autowired
    public GraphService(GraphDataService graphDataService) {
        this.graphDataService = graphDataService;
    }

    //TODO call this method before algorithm
    public void replaceGraphWithItsBiggestConnectedComponent(AlgorithmTask algorithmTask) {
        var graph = algorithmTask.getGraph();
        var nodeToEdgesIncidenceMap = graph.getIncidenceMap();

        var graphNodesFromConnectedComponent = findBiggestConnectedComponent(nodeToEdgesIncidenceMap);

        var nodeToEdgesIncidenceMapCopy = new HashMap<GraphNode, List<GraphEdge>>();

        for (Map.Entry<GraphNode, List<GraphEdge>> entry : nodeToEdgesIncidenceMap.entrySet()) {
            var graphEdgesList = entry.getValue();
            graphEdgesList.removeIf(graphEdge -> shouldGraphEdgeBeDeleted(graphNodesFromConnectedComponent, graphEdge));

            var graphNode = entry.getKey();
            if (shouldGraphNodeBeKept(graphNodesFromConnectedComponent, graphEdgesList, graphNode)) {
                nodeToEdgesIncidenceMapCopy.put(graphNode, graphEdgesList);
            }
        }

        graph.setNodeToEdgesIncidenceMap(nodeToEdgesIncidenceMapCopy);

        var graphDataDTO = algorithmTask.getGraphDataDTO();
        graphDataService.replaceGraphWithItsBiggestCommonComponent(graphDataDTO, graphNodesFromConnectedComponent);
    }

    public List<GraphNode> findBiggestConnectedComponent(Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap) {
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

    //TODO call this method before algorithm
    public Double[][] calculateShortestPathsDistances(Graph graph) {
        var nodeToEdgesIncidenceMap = graph.getIncidenceMap();

        var graphNodes = new ArrayList<>(nodeToEdgesIncidenceMap.keySet());

        int graphNodesCount = nodeToEdgesIncidenceMap.size();
        var shortestPathsDistances = new Double[graphNodesCount][graphNodesCount];

        //initialize distances with edge weights or infinity when edge doesn't exist
        for (int i = 0; i < graphNodesCount; i++) {
            for (int j = 0; j < graphNodesCount; j++) {
                if (i == j) {
                    shortestPathsDistances[i][j] = 0.0;
                } else {
                    double edgeWeight = getEdgeWeight(graphNodes.get(i), graphNodes.get(j), nodeToEdgesIncidenceMap);
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

        return shortestPathsDistances;
    }

    private boolean shouldGraphEdgeBeDeleted(List<GraphNode> graphNodesFromConnectedComponent, GraphEdge graphEdge) {
        return !graphNodesFromConnectedComponent.contains(graphEdge.getEndGraphNode());
    }

    private boolean shouldGraphNodeBeKept(List<GraphNode> graphNodesFromConnectedComponent,
                                          List<GraphEdge> graphEdgesList, GraphNode graphNode) {
        return graphNodesFromConnectedComponent.contains(graphNode) && !graphEdgesList.isEmpty();
    }

    private GraphNode getNodeForId(Long nodeId, Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap) {
        return nodeToEdgesIncidenceMap.keySet().stream()
                .filter(graphNode -> graphNode.getId().equals(nodeId))
                .findAny()
                .orElse(null);
    }

    private NodeDTO getCrossingWithId(List<NodeDTO> nodeDTOS, Long nodeId) {
        return nodeDTOS.stream()
                .filter(nodeDTO -> nodeDTO.getGeographicalNodeDTO().getId().equals(nodeId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Cannot find Crossing for GraphNode with given id: "
                        + nodeId));
    }

    private double getEdgeWeight(GraphNode graphNode1, GraphNode graphNode2,
                                 Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap) {
        GraphEdge graphEdgeToFind = nodeToEdgesIncidenceMap.get(graphNode1).stream()
                .filter(graphEdge -> graphEdge.getEndGraphNode().equals(graphNode2))
                .findFirst()
                .orElse(null);
        return graphEdgeToFind != null ? graphEdgeToFind.getWeight() : -1;
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

}
