package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.controllers.mapper.AlgorithmTaskMapper;
import com.agh.bsct.algorithm.entities.graph.GraphEdge;
import com.agh.bsct.algorithm.entities.graph.GraphNode;
import com.agh.bsct.algorithm.services.algorithms.AlgorithmFunctionsService;
import com.agh.bsct.algorithm.services.algorithms.AlgorithmService;
import com.agh.bsct.algorithm.services.entities.graph.GraphService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Qualifier(BFAlgorithm.BRUTE_FORCE_QUALIFIER)
public class BFAlgorithm implements IAlgorithm {

    static final String BRUTE_FORCE_QUALIFIER = "bruteForceAlgorithm";

    private AlgorithmFunctionsService algorithmFunctionsService;
    private AlgorithmService algorithmService;
    private AlgorithmTaskMapper algorithmTaskMapper;
    private GraphService graphService;


    @Autowired
    public BFAlgorithm(AlgorithmFunctionsService algorithmFunctionsService,
                       AlgorithmService algorithmService,
                       AlgorithmTaskMapper algorithmTaskMapper,
                       GraphService graphService) {
        this.algorithmFunctionsService = algorithmFunctionsService;
        this.algorithmService = algorithmService;
        this.algorithmTaskMapper = algorithmTaskMapper;
        this.graphService = graphService;
    }

    @Override
    public void run(AlgorithmTask algorithmTask) {
        // prepare properly graph
        algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING);
        graphService.replaceGraphWithItsBiggestConnectedComponent(algorithmTask);
        final Map<Long, Map<Long, Double>> shortestPathsDistances =
                graphService.calculateShortestPathsDistances(algorithmTask.getGraph());

        var bestState1 = getBestStateForOneHospital(algorithmTask, shortestPathsDistances);
        var bestState2 = getBestStateForTwoHospitals(algorithmTask, shortestPathsDistances);
        var bestState3 = getBestStateForThreeHospitals(algorithmTask, shortestPathsDistances);

        var numberOfResults = algorithmTask.getNumberOfResults();
        var bestState = (numberOfResults.equals(1)) ? bestState1 :
                ((numberOfResults.equals(2)) ? bestState2 : bestState3);

        // map to algorithm result and set it
        algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);
        algorithmTask.setHospitals(
                algorithmService.getGeographicalNodesForBestState(bestState, algorithmTask.getGraphDataDTO()));
        var fakeAlgorithmResult = algorithmTaskMapper.mapToAlgorithmResultDTO(algorithmTask);
        algorithmTask.setAlgorithmResultDTO(fakeAlgorithmResult);

    }

    private List<GraphNode> getBestStateForOneHospital(AlgorithmTask algorithmTask,
                                                       Map<Long, Map<Long, Double>> shortestPathsDistances) {
        Map<GraphNode, List<GraphEdge>> incidenceMap = algorithmTask.getGraph().getIncidenceMap();
        List<GraphNode> bestState = Collections.emptyList();
        double bestFunctionValue = Double.MAX_VALUE;

        for (var node : incidenceMap.keySet()) {
            List<GraphNode> acceptedState = Collections.singletonList(node);
            var acceptedFunctionValue = algorithmFunctionsService.calculateFunctionValue(
                    shortestPathsDistances, acceptedState);
            if (acceptedFunctionValue < bestFunctionValue) {
                bestFunctionValue = acceptedFunctionValue;
                bestState = acceptedState;
            }
        }

        //TODO to remove and replace with gnuplot
        System.out.println("1: " + bestFunctionValue);
        return bestState;
    }

    private List<GraphNode> getBestStateForTwoHospitals(AlgorithmTask algorithmTask, Map<Long,
            Map<Long, Double>> shortestPathsDistances) {
        Map<GraphNode, List<GraphEdge>> incidenceMap = algorithmTask.getGraph().getIncidenceMap();
        List<GraphNode> bestState = Collections.emptyList();
        double bestFunctionValue = Double.MAX_VALUE;

        for (var node1 : incidenceMap.keySet()) {
            for (var node2 : incidenceMap.keySet()) {
                List<GraphNode> acceptedState = Arrays.asList(node1, node2);
                var acceptedFunctionValue =
                        algorithmFunctionsService.calculateFunctionValue(shortestPathsDistances, acceptedState);
                if (acceptedFunctionValue < bestFunctionValue) {
                    bestFunctionValue = acceptedFunctionValue;
                    bestState = acceptedState;
                }
            }

        }

        //TODO to remove and replace with gnuplot
        System.out.println("2: " + bestFunctionValue);
        return bestState;
    }

    private List<GraphNode> getBestStateForThreeHospitals(AlgorithmTask algorithmTask,
                                                          Map<Long, Map<Long, Double>> shortestPathsDistances) {
        Map<GraphNode, List<GraphEdge>> incidenceMap = algorithmTask.getGraph().getIncidenceMap();
        List<GraphNode> bestState = Collections.emptyList();
        double bestFunctionValue = Double.MAX_VALUE;


        for (var node1 : incidenceMap.keySet()) {
            for (var node2 : incidenceMap.keySet()) {
                for (var node3 : incidenceMap.keySet()) {
                    List<GraphNode> acceptedState = Arrays.asList(node1, node2, node3);
                    var acceptedFunctionValue =
                            algorithmFunctionsService.calculateFunctionValue(shortestPathsDistances, acceptedState);
                    if (acceptedFunctionValue < bestFunctionValue) {
                        bestFunctionValue = acceptedFunctionValue;
                        bestState = acceptedState;
                    }
                }

            }
        }

        //TODO to remove and replace with gnuplot
        System.out.println("3: " + bestFunctionValue);
        return bestState;
    }

}