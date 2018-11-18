package com.agh.bsct.datacollector.entities.graph;

public class GraphEdge {

    /*
     * In GraphEdge there's no start node,
     * because the class is used only as an element of nodeToEdgedIncidenceMap in Graph class.
     * Start node is the key of the map.
     * It's like a ball (key) to which we want to add sticks with balls at the end (GraphEdges with end nodes).
     */

    private GraphNode endGraphNode;
    private double weight;

    GraphEdge(GraphNode endGraphNode, double weight) {
        this.endGraphNode = endGraphNode;
        this.weight = weight;
    }

    public GraphNode getEndGraphNode() {
        return endGraphNode;
    }

    public double getWeight() {
        return weight;
    }
}
