package com.agh.bsct.algorithm.entities.graph;

import java.util.List;
import java.util.Map;

public class Graph {

    private Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap;

    public Graph(Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap) {
        this.nodeToEdgesIncidenceMap = nodeToEdgesIncidenceMap;
    }

    public Map<GraphNode, List<GraphEdge>> getIncidenceMap() {
        return nodeToEdgesIncidenceMap;
    }

    public void setNodeToEdgesIncidenceMap(Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap) {
        this.nodeToEdgesIncidenceMap = nodeToEdgesIncidenceMap;
    }

}
