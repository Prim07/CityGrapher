package com.agh.bsct.datacollector.entities.graph;

public class GraphEdge {

    /*
     * W klasie GraphEdge nie ma początkowego node'a,
     * ponieważ jest ona używana jako element mapy nodeToEdgedIncidenceMap w klasie Graph.
     * Kluczem e tej mapie jest node początkowy.
     * Mamy kuleczkę (klucz) do której doczepiamy wykałaczki z kuleczkami na końcu(GraphEdge tylko z nodem końcowym).
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
