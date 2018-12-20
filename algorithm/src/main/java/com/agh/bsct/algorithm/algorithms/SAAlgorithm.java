package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.controllers.mapper.AlgorithmTaskMapper;
import com.agh.bsct.algorithm.entities.graph.GraphEdge;
import com.agh.bsct.algorithm.entities.graph.GraphNode;
import com.agh.bsct.algorithm.services.entities.graph.GraphService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
@Qualifier(SAAlgorithm.SIMULATED_ANNEALING_QUALIFIER)
public class SAAlgorithm implements IAlgorithm {

    public static final String SIMULATED_ANNEALING_QUALIFIER = "simulatedAnnealingAlgorithm";

    private static final int MAX_ITERATIONS = 10000;

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
        Map<Long, Map<Long, Double>> shortestPathsDistances = graphService.calculateShortestPathsDistances(algorithmTask.getGraph());

        // heart of calculating
        int k = 0;
        double temp = Double.MAX_VALUE;
        Map<GraphNode, List<GraphEdge>> incidenceMap = algorithmTask.getGraph().getIncidenceMap();
        List<GraphNode> globalState = initializeGlobalState(algorithmTask, incidenceMap);
        double globalFunctionValue = calculateFunctionValue(shortestPathsDistances, globalState);

        while (shouldIterate(k)) {
            double functionValue = calculateFunctionValue(shortestPathsDistances, globalState);
            changeRandomlyState(globalState, incidenceMap);
            double primFunctionValue = calculateFunctionValue(shortestPathsDistances, globalState);


            k++;
        }

        // map to algorithm result and set it
        var fakeAlgorithmResult = algorithmTaskMapper.mapToAlgorithmResultDTO(algorithmTask);
        algorithmTask.setAlgorithmResultDTO(fakeAlgorithmResult);
    }

    private boolean shouldIterate(int k) {
        return k < MAX_ITERATIONS;
    }

    private void changeRandomlyState(List<GraphNode> globalState, Map<GraphNode, List<GraphEdge>> incidenceMap) {
        int nodeToChangeIndex = random.nextInt(globalState.size());
        List<GraphEdge> nodeToChangeNeighbours = incidenceMap.get(globalState.get(nodeToChangeIndex));
        GraphEdge graphEdge = nodeToChangeNeighbours.get(random.nextInt(nodeToChangeNeighbours.size()));
        globalState.set(nodeToChangeIndex, graphEdge.getEndGraphNode());
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
