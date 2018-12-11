package com.agh.bsct.algorithm.services.runner;

import com.agh.bsct.algorithm.Algorithm;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncAlgorithmTaskRunner {


    @Async(Algorithm.SPRING_THREAD_POOL_NAME)
    public void run(AlgorithmTask algorithmTask) {
        //TODO implement here calculating and logic and remove below fake log calculations
        try {
            algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING);
            Thread.sleep(2000);
            algorithmTask.setJsonResult(algorithmTask.getGraphData());
            algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            algorithmTask.setStatus(AlgorithmCalculationStatus.FAILED);
        }
    }

}
