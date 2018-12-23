package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.controllers.mapper.AlgorithmTaskMapper;
import com.agh.bsct.algorithm.entities.graph.GraphEdge;
import com.agh.bsct.algorithm.entities.graph.GraphNode;
import com.agh.bsct.algorithm.services.algorithms.AlgorithmFunctionsService;
import com.agh.bsct.algorithm.services.algorithms.CrossingsService;
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
//@Primary
@Qualifier(BFAlgorithm.BRUTE_FORCE_QUALIFIER)
public class BFAlgorithm implements IAlgorithm {

    static final String BRUTE_FORCE_QUALIFIER = "bruteForceAlgorithm";

    private AlgorithmFunctionsService algorithmFunctionsService;
    private CrossingsService crossingsService;
    private AlgorithmTaskMapper algorithmTaskMapper;
    private GraphService graphService;


    @Autowired
    public BFAlgorithm(AlgorithmFunctionsService algorithmFunctionsService,
                       CrossingsService crossingsService,
                       AlgorithmTaskMapper algorithmTaskMapper,
                       GraphService graphService) {
        this.algorithmFunctionsService = algorithmFunctionsService;
        this.crossingsService = crossingsService;
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

        var bestState = getBestState(algorithmTask.getNumberOfResults(), algorithmTask, shortestPathsDistances);

        // map to algorithm result and set it
        algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);
        algorithmTask.setHospitals(
                crossingsService.getGeographicalNodesForBestState(bestState, algorithmTask.getGraphDataDTO()));
        var fakeAlgorithmResult = algorithmTaskMapper.mapToAlgorithmResultDTO(algorithmTask);
        algorithmTask.setAlgorithmResultDTO(fakeAlgorithmResult);

    }

    private List<GraphNode> getBestState(Integer numberOfResults, AlgorithmTask algorithmTask,
                                         Map<Long, Map<Long, Double>> shortestPathsDistances) {
        if (numberOfResults.equals(1)) {
            return getBestStateForOneHospital(algorithmTask, shortestPathsDistances);
        }
        if (numberOfResults.equals(2)) {
            return getBestStateForTwoHospitals(algorithmTask, shortestPathsDistances);
        }
        if (numberOfResults.equals(3)) {
            return getBestStateForThreeHospitals(algorithmTask, shortestPathsDistances);
        }
        throw new IllegalStateException("Brute Force Algorithm cannot be applied to " + numberOfResults
                + " requested results. Please choose 1, 2 or 3.");
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
