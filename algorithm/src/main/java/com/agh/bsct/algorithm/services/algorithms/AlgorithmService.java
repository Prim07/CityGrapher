package com.agh.bsct.algorithm.services.algorithms;

import com.agh.bsct.algorithm.entities.graph.GraphNode;
import com.agh.bsct.api.entities.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import com.agh.bsct.api.entities.graphdata.NodeDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AlgorithmService {

    public double calculateFunctionValue(Map<Long, Map<Long, Double>> shortestPathsDistances,
                                         List<GraphNode> globalState) {
        var distancesToClosestHospitalsSum = 0.0;

        for (Map<Long, Double> currentNodeShortestPathsDistance : shortestPathsDistances.values()) {
            var distanceToClosestHospitals = Double.MAX_VALUE;
            for (var currentGlobalStateNodeId : globalState) {
                var distanceToHospital = currentNodeShortestPathsDistance.get(currentGlobalStateNodeId.getId());
                if (distanceToClosestHospitals > distanceToHospital) {
                    distanceToClosestHospitals = distanceToHospital;
                }
            }
            distancesToClosestHospitalsSum += distanceToClosestHospitals;
        }

        return distancesToClosestHospitalsSum;
    }


    public List<GeographicalNodeDTO> getGeographicalNodesForBestState(List<GraphNode> bestState,
                                                                      GraphDataDTO graphDataDTO) {
        List<Long> bestStateNodesIds = bestState.stream()
                .map(GraphNode::getId)
                .collect(Collectors.toList());
        return graphDataDTO.getNodeDTOS().stream()
                .map(NodeDTO::getGeographicalNodeDTO)
                .filter(geographicalNodeDTO -> bestStateNodesIds.contains(geographicalNodeDTO.getId()))
                .collect(Collectors.toList());
    }

}
