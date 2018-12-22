package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.algorithms.outputwriter.GnuplotOutputWriter;
import com.agh.bsct.algorithm.controllers.mapper.AlgorithmTaskMapper;
import com.agh.bsct.algorithm.entities.graph.GraphEdge;
import com.agh.bsct.algorithm.entities.graph.GraphNode;
import com.agh.bsct.algorithm.services.algorithms.AlgorithmService;
import com.agh.bsct.algorithm.services.entities.graph.GraphService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Qualifier(BFAlgorithm.BRUTE_FORCE_QUALIFIER)
public class BFAlgorithm implements IAlgorithm {

    static final String BRUTE_FORCE_QUALIFIER = "bruteForceAlgorithm";

    private AlgorithmService algorithmService;
    private AlgorithmTaskMapper algorithmTaskMapper;
    private GraphService graphService;


    @Autowired
    public BFAlgorithm(AlgorithmService algorithmService,
                       AlgorithmTaskMapper algorithmTaskMapper,
                       GraphService graphService,
                       GnuplotOutputWriter gnuplotOutputWriter) {
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

        var bestState1 = getBestStateForOneHospitals(algorithmTask, shortestPathsDistances);
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

    private List<GraphNode> getBestStateForOneHospitals(AlgorithmTask algorithmTask, Map<Long, Map<Long, Double>> shortestPathsDistances) {
        Map<GraphNode, List<GraphEdge>> incidenceMap = algorithmTask.getGraph().getIncidenceMap();
        List<GraphNode> acceptedState;
        List<GraphNode> bestState = null;
        double acceptedFunctionValue;
        double bestFunctionValue = Double.MAX_VALUE;

        for (var node : incidenceMap.keySet()) {
            acceptedState = new ArrayList<>();
            acceptedState.add(node);
            acceptedFunctionValue =
                    algorithmService.calculateFunctionValue(shortestPathsDistances, acceptedState);
            if (acceptedFunctionValue < bestFunctionValue) {
                bestFunctionValue = acceptedFunctionValue;
                bestState = acceptedState;
            }
        }

        System.out.println("1: " + bestFunctionValue);
        return bestState;
    }

    private List<GraphNode> getBestStateForTwoHospitals(AlgorithmTask algorithmTask, Map<Long, Map<Long, Double>> shortestPathsDistances) {
        Map<GraphNode, List<GraphEdge>> incidenceMap = algorithmTask.getGraph().getIncidenceMap();
        List<GraphNode> acceptedState;
        List<GraphNode> bestState = null;
        double acceptedFunctionValue;
        double bestFunctionValue = Double.MAX_VALUE;

        for (var node1 : incidenceMap.keySet()) {
            for (var node2 : incidenceMap.keySet()) {
                acceptedState = new ArrayList<>();
                acceptedState.add(node1);
                acceptedState.add(node2);
                acceptedFunctionValue =
                        algorithmService.calculateFunctionValue(shortestPathsDistances, acceptedState);
                if (acceptedFunctionValue < bestFunctionValue) {
                    bestFunctionValue = acceptedFunctionValue;
                    bestState = acceptedState;
                }
            }

        }

        System.out.println("2: " + bestFunctionValue);
        return bestState;
    }

    private List<GraphNode> getBestStateForThreeHospitals(AlgorithmTask algorithmTask, Map<Long, Map<Long, Double>> shortestPathsDistances) {
        Map<GraphNode, List<GraphEdge>> incidenceMap = algorithmTask.getGraph().getIncidenceMap();
        List<GraphNode> acceptedState;
        List<GraphNode> bestState = null;
        double acceptedFunctionValue;
        double bestFunctionValue = Double.MAX_VALUE;

        for (var node1 : incidenceMap.keySet()) {
            for (var node2 : incidenceMap.keySet()) {
                for (var node3 : incidenceMap.keySet()) {
                    acceptedState = new ArrayList<>();
                    acceptedState.add(node1);
                    acceptedState.add(node2);
                    acceptedState.add(node3);
                    acceptedFunctionValue =
                            algorithmService.calculateFunctionValue(shortestPathsDistances, acceptedState);
                    if (acceptedFunctionValue < bestFunctionValue) {
                        bestFunctionValue = acceptedFunctionValue;
                        bestState = acceptedState;
                    }
                }

            }
        }

        System.out.println("3: " + bestFunctionValue);
        return bestState;
    }

}
