package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.algorithms.outputwriter.GnuplotOutputWriter;
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
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
@Primary
@Qualifier(SAAlgorithm.SIMULATED_ANNEALING_QUALIFIER)
public class SAAlgorithm implements IAlgorithm {

    static final String SIMULATED_ANNEALING_QUALIFIER = "simulatedAnnealingAlgorithm";

    private static final int MAX_ITERATIONS = 1000000;
    private static final double MIN_TEMP = 0.000000005;
    private static final double INITIAL_TEMP = 100000000.0;

    private AlgorithmFunctionsService algorithmFunctionsService;
    private AlgorithmService algorithmService;
    private AlgorithmTaskMapper algorithmTaskMapper;
    private GraphService graphService;
    private Random random;
    private GnuplotOutputWriter gnuplotOutputWriter;


    @Autowired
    public SAAlgorithm(AlgorithmFunctionsService algorithmFunctionsService,
                       AlgorithmService algorithmService,
                       AlgorithmTaskMapper algorithmTaskMapper,
                       GraphService graphService,
                       GnuplotOutputWriter gnuplotOutputWriter) {
        this.algorithmFunctionsService = algorithmFunctionsService;
        this.algorithmService = algorithmService;
        this.algorithmTaskMapper = algorithmTaskMapper;
        this.graphService = graphService;
        this.gnuplotOutputWriter = gnuplotOutputWriter;
        this.random = new Random();
    }

    @Override
    public void run(AlgorithmTask algorithmTask) {
        // prepare properly graph
        algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING);
        graphService.replaceGraphWithItsBiggestConnectedComponent(algorithmTask);
        final Map<Long, Map<Long, Double>> shortestPathsDistances =
                graphService.calculateShortestPathsDistances(algorithmTask.getGraph());

        // prepare AlgorithmOutputWriter (example version)
        gnuplotOutputWriter.initializeResources(algorithmTask.getTaskId());

        // heart of calculating
        var k = 0;
        var temp = INITIAL_TEMP;
        Map<GraphNode, List<GraphEdge>> incidenceMap = algorithmTask.getGraph().getIncidenceMap();
        List<GraphNode> acceptedState = initializeGlobalState(algorithmTask, incidenceMap);
        List<GraphNode> bestState = acceptedState;
        double acceptedFunctionValue = algorithmFunctionsService.calculateFunctionValue(shortestPathsDistances, acceptedState);
        double bestFunctionValue = acceptedFunctionValue;

        while (shouldIterate(k, temp)) {
            acceptedFunctionValue = algorithmFunctionsService.calculateFunctionValue(shortestPathsDistances, acceptedState);
            var localState = changeRandomlyState(incidenceMap, acceptedState);
            var localFunctionValue = algorithmFunctionsService.calculateFunctionValue(shortestPathsDistances, localState);
            double delta = localFunctionValue - acceptedFunctionValue;

            if (localFunctionValue < acceptedFunctionValue) {
                acceptedState = localState;
                if (localFunctionValue < bestFunctionValue) {
                    bestFunctionValue = localFunctionValue;
                }
            } else {
                var worseResultAcceptanceProbability = random.nextDouble();
                var p = Math.exp(-delta / temp);
                if (worseResultAcceptanceProbability < p) {
                    acceptedState = localState;
                }
            }

            // update temperature
            temp = 0.99 * temp;
            k++;

            gnuplotOutputWriter.writeLineIfEnabled(k, temp);
        }
        System.out.println(temp);
        System.out.println(k);
        System.out.println(bestFunctionValue);

        //close writer resources
        gnuplotOutputWriter.closeResources();

        // map to algorithm result and set it
        algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);
        algorithmTask.setHospitals(
                algorithmService.getGeographicalNodesForBestState(bestState, algorithmTask.getGraphDataDTO()));
        var fakeAlgorithmResult = algorithmTaskMapper.mapToAlgorithmResultDTO(algorithmTask);
        algorithmTask.setAlgorithmResultDTO(fakeAlgorithmResult);
    }

    private boolean shouldIterate(int k, double temp) {
        return k < MAX_ITERATIONS
                && temp > MIN_TEMP;
    }

    private ArrayList<GraphNode> changeRandomlyState(Map<GraphNode, List<GraphEdge>> incidenceMap,
                                                     List<GraphNode> globalState) {
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

}
