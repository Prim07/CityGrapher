package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.controllers.mapper.AlgorithmTaskMapper;
import com.agh.bsct.algorithm.entities.graph.GraphEdge;
import com.agh.bsct.algorithm.entities.graph.GraphNode;
import com.agh.bsct.algorithm.services.entities.graph.GraphService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.api.entities.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import com.agh.bsct.api.entities.graphdata.NodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@Qualifier(SAAlgorithm.SIMULATED_ANNEALING_QUALIFIER)
public class SAAlgorithm implements IAlgorithm {

    public static final String SIMULATED_ANNEALING_QUALIFIER = "simulatedAnnealingAlgorithm";

    private static final int MAX_ITERATIONS = 100000;
    private static final double MIN_TEMP = 0.005;

    private AlgorithmTaskMapper algorithmTaskMapper;
    private GraphService graphService;
    private Random random;

    @Autowired
    public SAAlgorithm(AlgorithmTaskMapper algorithmTaskMapper, GraphService graphService) {
        this.algorithmTaskMapper = algorithmTaskMapper;
        this.graphService = graphService;
        this.random = new Random();
    }

    @Override
    public void run(AlgorithmTask algorithmTask) {
        // prepare properly graph
        algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING);
        graphService.replaceGraphWithItsBiggestConnectedComponent(algorithmTask);
        final Map<Long, Map<Long, Double>> shortestPathsDistances = graphService.calculateShortestPathsDistances(algorithmTask.getGraph());

        // heart of calculating
        var k = 0;
        var temp = 10000.0;
        Map<GraphNode, List<GraphEdge>> incidenceMap = algorithmTask.getGraph().getIncidenceMap();
        List<GraphNode> acceptedState = initializeGlobalState(algorithmTask, incidenceMap);
        List<GraphNode> bestState = acceptedState;
        double acceptedFunctionValue = calculateFunctionValue(shortestPathsDistances, acceptedState);
        double bestFunctionValue = acceptedFunctionValue;

        while (shouldIterate(k, temp)) {
            acceptedFunctionValue = calculateFunctionValue(shortestPathsDistances, acceptedState);
            var localState = changeRandomlyState(incidenceMap, acceptedState);
            var localFunctionValue = calculateFunctionValue(shortestPathsDistances, localState);

            if (localFunctionValue < acceptedFunctionValue) {
                acceptedState = localState;
                if (localFunctionValue < bestFunctionValue) {
                    bestFunctionValue = localFunctionValue;
                }
            } else {
                var worseResultAcceptanceProbability = random.nextDouble();
                var p = Math.exp(-(localFunctionValue - acceptedFunctionValue) / temp);
                if (worseResultAcceptanceProbability < p) {
                    acceptedState = localState;
                }
            }

            // update temperature
            temp = temp * 0.95;
            k++;
        }

        // map to algorithm result and set it
        algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);
        algorithmTask.setHospitals(getGeographicalNodesForBestState(bestState, algorithmTask.getGraphDataDTO()));
        var fakeAlgorithmResult = algorithmTaskMapper.mapToAlgorithmResultDTO(algorithmTask);
        algorithmTask.setAlgorithmResultDTO(fakeAlgorithmResult);
    }

    private List<GeographicalNodeDTO> getGeographicalNodesForBestState(List<GraphNode> bestState,
                                                                       GraphDataDTO graphDataDTO) {
        List<Long> bestStateNodesIds = bestState.stream()
                .map(GraphNode::getId)
                .collect(Collectors.toList());
        return graphDataDTO.getNodeDTOS().stream()
                .map(NodeDTO::getGeographicalNodeDTO)
                .filter(geographicalNodeDTO -> bestStateNodesIds.contains(geographicalNodeDTO.getId()))
                .collect(Collectors.toList());
    }

    private boolean shouldIterate(int k, double temp) {
        return k < MAX_ITERATIONS
                && temp > MIN_TEMP;
    }

    private ArrayList<GraphNode> changeRandomlyState(Map<GraphNode, List<GraphEdge>> incidenceMap, List<GraphNode> globalState) {
        var localState = new ArrayList<>(globalState);
        int nodeToChangeIndex = random.nextInt(localState.size());
        List<GraphEdge> nodeToChangeNeighbours = incidenceMap.get(localState.get(nodeToChangeIndex));
        GraphEdge graphEdge = nodeToChangeNeighbours.get(random.nextInt(nodeToChangeNeighbours.size()));
        localState.set(nodeToChangeIndex, graphEdge.getEndGraphNode());
        return localState;
    }

    private List<GraphNode> initializeGlobalState(AlgorithmTask algorithmTask,
                                                  Map<GraphNode, List<GraphEdge>> incidenceMap) {
        ArrayList<GraphNode> graphNodesList = new ArrayList<>(incidenceMap.keySet());

        Integer numberOfResults = algorithmTask.getNumberOfResults();
        List<GraphNode> globalState = new ArrayList<>(numberOfResults);

        for (var i = 0; i < numberOfResults; ++i) {
            int chosenNodeId = random.nextInt(algorithmTask.getGraph().getIncidenceMap().size());
            while (globalState.contains(graphNodesList.get(chosenNodeId))) {
                chosenNodeId = random.nextInt(algorithmTask.getGraph().getIncidenceMap().size());
            }
            globalState.add(i, graphNodesList.get(chosenNodeId));
        }

        return globalState;
    }

    private double calculateFunctionValue(Map<Long, Map<Long, Double>> shortestPathsDistances,
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

}
