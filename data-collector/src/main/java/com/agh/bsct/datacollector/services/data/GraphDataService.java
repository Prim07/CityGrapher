package com.agh.bsct.datacollector.services.data;

import com.agh.bsct.api.entities.citydata.CityDataDTO;
import com.agh.bsct.api.entities.citydata.StreetDTO;
import com.agh.bsct.api.entities.graphdata.EdgeDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import com.agh.bsct.api.entities.graphdata.NodeDTO;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.*;

@Service
public class GraphDataService {

    private static final int EARTH_RADIUS = 6372800;
    private static final int NODE_WEIGHT_MIN = 50;
    private static final int NODE_WEIGHT_MAX = 1000;

    private static final Random random = new Random();

    public GraphDataDTO getGraphData(CityDataDTO cityData) {
        List<EdgeDTO> edges = calculateEdgeWeights(cityData.getStreets(), cityData.getNodes());
        List<NodeDTO> crossings = calculateNodeWeights(cityData.getNodes());
        return new GraphDataDTO(edges, crossings);
    }

    private List<EdgeDTO> calculateEdgeWeights(List<StreetDTO> streets, List<com.agh.bsct.api.entities.citydata.NodeDTO> nodes) {
        List<EdgeDTO> edges = new ArrayList<>();

        for (StreetDTO street : streets) {
            double weight = 0;
            List<Long> nodesIds = street.getNodesIds();

            for (int i = 0; i < nodesIds.size() - 1; i++) {
                Long startNodeId = nodesIds.get(i);
                Long endNodeId = nodesIds.get(i + 1);

                com.agh.bsct.api.entities.citydata.NodeDTO startNode = getNodeWithId(startNodeId, nodes);
                com.agh.bsct.api.entities.citydata.NodeDTO endNode = getNodeWithId(endNodeId, nodes);

                weight += calculateDistance(startNode, endNode);
            }

            edges.add(new EdgeDTO(street, weight));
        }

        return edges;
    }

    private double calculateDistance(com.agh.bsct.api.entities.citydata.NodeDTO startNode, com.agh.bsct.api.entities.citydata.NodeDTO endNode) {
        double startLat = startNode.getLat();
        double endLat = endNode.getLat();
        double startLon = startNode.getLon();
        double endLon = endNode.getLon();

        double startLatRad = toRadians(startLat);
        double endLatRad = toRadians(endLat);
        double latRad = toRadians(endLat - startLat);
        double lonRad = toRadians(endLon - startLon);

        return 2.0 * EARTH_RADIUS
                * asin(sqrt(pow(sin(latRad / 2), 2) + cos(startLatRad) * cos(endLatRad) * pow(sin(lonRad / 2), 2)));
    }

    private com.agh.bsct.api.entities.citydata.NodeDTO getNodeWithId(Long nodeId, List<com.agh.bsct.api.entities.citydata.NodeDTO> nodes) {
        return nodes.stream()
                .filter(node -> node.getId().equals(nodeId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Cannot find GraphNode with given id: " + nodeId));
    }

    private List<NodeDTO> calculateNodeWeights(List<com.agh.bsct.api.entities.citydata.NodeDTO> nodes) {
        //TODO sprawdzić czas wykonania i porównać z czymś szybszym, np. zwykłym forem
        return nodes.stream()
                .map(this::getCrossingWithRandomNodeWeight)
                .collect(Collectors.toList());
    }

    private NodeDTO getCrossingWithRandomNodeWeight(com.agh.bsct.api.entities.citydata.NodeDTO node) {
        return new NodeDTO(node, random.nextInt((NODE_WEIGHT_MAX - NODE_WEIGHT_MIN) + 1) + NODE_WEIGHT_MIN);
    }

    public List<com.agh.bsct.api.entities.citydata.NodeDTO> runAlgorithmAndCalculateHospitalNodes(ObjectNode jsonGraph) {
        //TODO impl
        return new ArrayList<>();
    }
}
