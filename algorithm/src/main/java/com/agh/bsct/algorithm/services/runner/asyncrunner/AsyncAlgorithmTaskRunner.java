package com.agh.bsct.algorithm.services.runner.asyncrunner;

import com.agh.bsct.algorithm.Algorithm;
import com.agh.bsct.algorithm.controllers.mapper.AlgorithmTaskMapper;
import com.agh.bsct.algorithm.services.entities.graphdata.GraphDataService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AsyncAlgorithmTaskRunner {

    private static AtomicInteger THREAD_COUNT = new AtomicInteger(0);
    private AlgorithmTaskMapper algorithmTaskMapper;
    private GraphDataService graphDataService;

    @Autowired
    public AsyncAlgorithmTaskRunner(AlgorithmTaskMapper algorithmTaskMapper, GraphDataService graphDataService) {
        this.algorithmTaskMapper = algorithmTaskMapper;
        this.graphDataService = graphDataService;
    }

    @Async(Algorithm.SPRING_THREAD_POOL_NAME)
    public Future<Integer> run(AlgorithmTask algorithmTask) {
        int currentThreadNumber = THREAD_COUNT.getAndIncrement();

        //TODO implement here calculating and logic and remove below fake log calculations
        try {
            algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING);
            Thread.sleep(2000);
            algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);

            extractBiggestConnectedComponent(algorithmTask);

            var fakeAlgorithmResult = algorithmTaskMapper.mapToAlgorithmResultDTO(algorithmTask);
            algorithmTask.setAlgorithmResultDTO(fakeAlgorithmResult);
        } catch (InterruptedException e) {
            System.out.println("Interrupted thread: " + currentThreadNumber);
            Thread.currentThread().interrupt();
            algorithmTask.setStatus(AlgorithmCalculationStatus.CANCELLED);
        }

        return new AsyncResult<>(currentThreadNumber);
    }

    private void extractBiggestConnectedComponent(AlgorithmTask algorithmTask) {
        graphDataService.extractBiggestConnectedComponent(algorithmTask.getGraphDataDTO(),
                algorithmTask.getGraph().getIncidenceMap());
    }

}
