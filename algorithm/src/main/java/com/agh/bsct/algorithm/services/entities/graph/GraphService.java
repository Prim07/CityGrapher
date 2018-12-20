package com.agh.bsct.algorithm.services.entities.graph;

import com.agh.bsct.algorithm.entities.graph.Graph;
import com.agh.bsct.algorithm.entities.graph.GraphEdge;
import com.agh.bsct.algorithm.entities.graph.GraphNode;
import com.agh.bsct.algorithm.services.entities.graphdata.GraphDataService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
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

    public Map<Long, Map<Long, Double>> calculateShortestPathsDistances(Graph graph) {
        var nodeToEdgesIncidenceMap = graph.getIncidenceMap();

        var graphNodes = new ArrayList<>(nodeToEdgesIncidenceMap.keySet());
        var shortestPathsDistances = new HashMap<Long, Map<Long, Double>>();

        for (GraphNode i : graphNodes) {
            for (GraphNode j : graphNodes) {
                if (i.equals(j)) {
                    putValueToMap(i, j, 0.0, shortestPathsDistances);
                } else {
                    double edgeWeight = getEdgeWeight(i, j, nodeToEdgesIncidenceMap);
                    if (edgeWeight > 0) {
                        putValueToMap(i, j, edgeWeight, shortestPathsDistances);

                    } else {
                        putValueToMap(i, j, Double.MAX_VALUE, shortestPathsDistances);

                    }
                }
            }
        }
        for (GraphNode k : graphNodes) {
            for (GraphNode i : graphNodes) {
                for (GraphNode j : graphNodes) {
                    if (shortestPathsDistances.get(i.getId()).get(j.getId()) >
                            shortestPathsDistances.get(i.getId()).get(k.getId())
                                    + shortestPathsDistances.get(k.getId()).get(j.getId())) {
                        shortestPathsDistances.get(i.getId()).put(j.getId(),
                                shortestPathsDistances.get(i.getId()).get(k.getId())
                                        + shortestPathsDistances.get(k.getId()).get(j.getId()));
                    }
                }
            }
        }

        return shortestPathsDistances;
    }

    private void putValueToMap(GraphNode i,
                               GraphNode j,
                               double value,
                               Map<Long, Map<Long, Double>> shortestPathsDistances) {
        Long iId = i.getId();
        Map<Long, Double> currentNodeShortestPathsDistance = shortestPathsDistances.get(iId);
        if (currentNodeShortestPathsDistance == null) {
            shortestPathsDistances.put(iId, new HashMap<>());
            currentNodeShortestPathsDistance = shortestPathsDistances.get(iId);
        }
        currentNodeShortestPathsDistance.put(j.getId(), value);
    }

    private boolean shouldGraphEdgeBeDeleted(List<GraphNode> graphNodesFromConnectedComponent, GraphEdge graphEdge) {
        return !graphNodesFromConnectedComponent.contains(graphEdge.getEndGraphNode());
    }

    private boolean shouldGraphNodeBeKept(List<GraphNode> graphNodesFromConnectedComponent,
                                          List<GraphEdge> graphEdgesList, GraphNode graphNode) {
        return graphNodesFromConnectedComponent.contains(graphNode) && !graphEdgesList.isEmpty();
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
