package com.agh.bsct.algorithm.services.runner;

import com.agh.bsct.algorithm.Algorithm;
import com.agh.bsct.algorithm.controllers.mapper.AlgorithmTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncAlgorithmTaskRunner {


    private AlgorithmTaskMapper algorithmTaskMapper;

    @Autowired
    public AsyncAlgorithmTaskRunner(AlgorithmTaskMapper algorithmTaskMapper) {
        this.algorithmTaskMapper = algorithmTaskMapper;
    }

    @Async(Algorithm.SPRING_THREAD_POOL_NAME)
    public void run(AlgorithmTask algorithmTask) {
        //TODO implement here calculating and logic and remove below fake log calculations
        try {
            algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING);
            Thread.sleep(2000);
            algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);

            var fakeAlgorithmResult = algorithmTaskMapper.mapToAlgorithmResultDTO(algorithmTask);
            algorithmTask.setAlgorithmResultDTO(fakeAlgorithmResult);
        } catch (InterruptedException e) {
            e.printStackTrace();
            algorithmTask.setStatus(AlgorithmCalculationStatus.FAILED);
        }
    }

}
