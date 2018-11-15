package com.agh.bsct.datacollector.services.data;

import com.agh.bsct.datacollector.entities.citydata.CityData;
import com.agh.bsct.datacollector.entities.citydata.Node;
import com.agh.bsct.datacollector.entities.citydata.Street;
import com.agh.bsct.datacollector.entities.graphdata.Crossing;
import com.agh.bsct.datacollector.entities.graphdata.Edge;
import com.agh.bsct.datacollector.entities.graphdata.GraphData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.*;

@Service
public class GraphDataService {

    private static final int EARTH_RADIUS = 6372800;
    private static final int DEFAULT_NODE_WEIGHT = 1;

    public GraphData getGraphData(CityData cityData) {
        List<Edge> edges = calculateEdgeWeights(cityData.getStreets(), cityData.getNodes());
        List<Crossing> crossings = calculateNodeWeights(cityData.getNodes());
        return new GraphData(edges, crossings);
    }

    private List<Edge> calculateEdgeWeights(List<Street> streets, List<Node> nodes) {
        List<Edge> edges = new ArrayList<>();

        for (Street street : streets) {
            double weight = 0;

            List<Long> nodesIds = street.getNodesIds();
            for (int i = 0; i < nodesIds.size() - 1; i++) {
                Long startNodeId = nodesIds.get(i);
                Long endNodeId = nodesIds.get(i + 1);

                Node startNode = getNodeWithId(startNodeId, nodes);
                Node endNode = getNodeWithId(endNodeId, nodes);

                weight += calculateDistance(startNode, endNode);
            }

            edges.add(new Edge(street, weight));
        }

        return edges;
    }

    private double calculateDistance(Node startNode, Node endNode) {
        double startLat = startNode.getLat();
        double endLat = endNode.getLat();
        double startLon = startNode.getLon();
        double endLon = endNode.getLon();

        double startLatRad = toRadians(startLat);
        double endLatRad = toRadians(endLat);
        double latRad = toRadians(endLat - startLat);
        double lonRad = toRadians(endLon - startLon);

        return 2.0 * EARTH_RADIUS * asin(sqrt(pow(sin(latRad / 2), 2) + cos(startLatRad) * cos(endLatRad) * pow(sin(lonRad / 2), 2)));
    }

    private Node getNodeWithId(Long nodeId, List<Node> nodes) {
        return nodes.stream()
                .filter(node -> node.getId().equals(nodeId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Cannot find Node with given id: " + nodeId));
    }

    private List<Crossing> calculateNodeWeights(List<Node> nodes) {
        //TODO sprawdzić czas wykonania i porównać z czymś szybszym, np. zwykłym forem
        return nodes.stream()
                .map((node -> new Crossing(node, DEFAULT_NODE_WEIGHT)))
                .collect(Collectors.toList());
    }

    public List<Node> runAlgorithmAndCalculateHospitalNodes(GraphData graphData) {
        //TODO impl
        return new ArrayList<>();
    }
}
