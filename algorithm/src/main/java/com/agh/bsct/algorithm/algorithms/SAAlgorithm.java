package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.algorithms.outputwriter.GnuplotOutputWriter;
import com.agh.bsct.algorithm.controllers.mapper.AlgorithmTaskMapper;
import com.agh.bsct.algorithm.entities.graph.GraphEdge;
import com.agh.bsct.algorithm.entities.graph.GraphNode;
import com.agh.bsct.algorithm.services.algorithms.AlgorithmFunctionsService;
import com.agh.bsct.algorithm.services.algorithms.CrossingsService;
import com.agh.bsct.algorithm.services.entities.graph.GraphService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.apache.commons.collections4.queue.CircularFifoQueue;
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

    static final String SIMULATED_ANNEALING_QUALIFIER = "simulatedAnnealingAlgorithm";

    private static final double INITIAL_TEMP = 100000000.0;
    private static final double ALPHA = 0.9999;

    private AlgorithmFunctionsService algorithmFunctionsService;
    private CrossingsService crossingsService;
    private AlgorithmTaskMapper algorithmTaskMapper;
    private GraphService graphService;
    private Random random;
    private GnuplotOutputWriter gnuplotOutputWriter;


    @Autowired
    public SAAlgorithm(AlgorithmFunctionsService algorithmFunctionsService,
                       CrossingsService crossingsService,
                       AlgorithmTaskMapper algorithmTaskMapper,
                       GraphService graphService,
                       GnuplotOutputWriter gnuplotOutputWriter) {
        this.algorithmFunctionsService = algorithmFunctionsService;
        this.crossingsService = crossingsService;
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

        //initialize collection with 100x1
        var lastHundredChanges = initializeLastHundredChanges();

        List<GraphNode> acceptedState = initializeGlobalState(algorithmTask, incidenceMap);
        List<GraphNode> bestState = acceptedState;
        double acceptedFunctionValue =
                algorithmFunctionsService.calculateFunctionValue(shortestPathsDistances, acceptedState);
        double bestFunctionValue = acceptedFunctionValue;


        while (shouldIterate(lastHundredChanges)) {
            acceptedFunctionValue =
                    algorithmFunctionsService.calculateFunctionValue(shortestPathsDistances, acceptedState);
            var localState = changeRandomlyState(incidenceMap, acceptedState);
            var localFunctionValue =
                    algorithmFunctionsService.calculateFunctionValue(shortestPathsDistances, localState);
            double delta = localFunctionValue - acceptedFunctionValue;

            if (localFunctionValue < acceptedFunctionValue) {
                //change has been made
                lastHundredChanges.add(1);
                acceptedState = localState;
                if (localFunctionValue < bestFunctionValue) {
                    bestFunctionValue = localFunctionValue;
                    bestState = acceptedState;
                }
            } else {
                var worseResultAcceptanceProbability = random.nextDouble();
                var p = Math.exp(-delta / temp);
                if (worseResultAcceptanceProbability < p) {
                    //change has been made
                    lastHundredChanges.add(1);
                    acceptedState = localState;
                } else {
                    //change hasn't been made
                    lastHundredChanges.add(0);
                }
            }

            // update temperature
            temp = ALPHA * temp;
            k++;

            gnuplotOutputWriter.writeLineIfEnabled(k, temp, Math.abs(delta), localFunctionValue, acceptedFunctionValue, bestFunctionValue);
        }
        System.out.println("temp: " + temp);
        System.out.println("k: " + k);
        System.out.println("best FV: " + bestFunctionValue);
        System.out.println("accepted FV: " + acceptedFunctionValue);

        //close writer resources
        gnuplotOutputWriter.closeResources();

        // map to algorithm result and set it
        algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);
        algorithmTask.setHospitals(
                crossingsService.getGeographicalNodesForBestState(bestState, algorithmTask.getGraphDataDTO()));
        var fakeAlgorithmResult = algorithmTaskMapper.mapToAlgorithmResultDTO(algorithmTask);
        algorithmTask.setAlgorithmResultDTO(fakeAlgorithmResult);
    }

    private CircularFifoQueue<Integer> initializeLastHundredChanges() {
        var lastHundredChanges = new CircularFifoQueue<Integer>(100);
        for (var i = 0; i < 100; i++) {
            lastHundredChanges.add(1);
        }
        return lastHundredChanges;
    }

    private boolean shouldIterate(CircularFifoQueue<Integer> lastHundredChanges) {
        return lastHundredChanges.stream().mapToInt(Integer::intValue).sum() > 0;
    }

    private ArrayList<GraphNode> changeRandomlyState(Map<GraphNode, List<GraphEdge>> incidenceMap,
                                                     List<GraphNode> globalState) {
        var localState = new ArrayList<>(globalState);
        var nodeToChangeIndex = random.nextInt(localState.size());
        List<GraphEdge> nodeToChangeNeighbours = incidenceMap.get(localState.get(nodeToChangeIndex));
        var graphEdge = nodeToChangeNeighbours.get(random.nextInt(nodeToChangeNeighbours.size()));
        var endGraphNode = graphEdge.getEndGraphNode();
        while (localState.contains(endGraphNode)) {
            nodeToChangeIndex = random.nextInt(localState.size());
            nodeToChangeNeighbours = incidenceMap.get(localState.get(nodeToChangeIndex));
            graphEdge = nodeToChangeNeighbours.get(random.nextInt(nodeToChangeNeighbours.size()));
            endGraphNode = graphEdge.getEndGraphNode();
        }
        localState.set(nodeToChangeIndex, endGraphNode);
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
