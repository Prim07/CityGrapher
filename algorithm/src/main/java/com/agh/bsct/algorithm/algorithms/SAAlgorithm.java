package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.controllers.mapper.AlgorithmTaskMapper;
import com.agh.bsct.algorithm.services.entities.graphdata.GraphDataService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(SAAlgorithm.SIMULATED_ANNEALING_QUALIFIER)
public class SAAlgorithm implements IAlgorithm {

    public static final String SIMULATED_ANNEALING_QUALIFIER = "simulatedAnnealingAlgorithm";

    private AlgorithmTaskMapper algorithmTaskMapper;
    private GraphDataService graphDataService;

    @Autowired
    public SAAlgorithm(AlgorithmTaskMapper algorithmTaskMapper, GraphDataService graphDataService) {
        this.algorithmTaskMapper = algorithmTaskMapper;
        this.graphDataService = graphDataService;
    }

    @Override
    public void run(AlgorithmTask algorithmTask) {
        algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING);

        algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);

        extractBiggestConnectedComponent(algorithmTask);

        var fakeAlgorithmResult = algorithmTaskMapper.mapToAlgorithmResultDTO(algorithmTask);
        algorithmTask.setAlgorithmResultDTO(fakeAlgorithmResult);
    }

    private void extractBiggestConnectedComponent(AlgorithmTask algorithmTask) {
        graphDataService.extractBiggestConnectedComponent(algorithmTask.getGraphDataDTO(),
                algorithmTask.getGraph().getIncidenceMap());
    }

}
