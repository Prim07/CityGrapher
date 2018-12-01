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
            setStatus(algorithmTask, AlgorithmCalculationStatus.CALCULATING);
            Thread.sleep(2000);
            setStatus(algorithmTask, AlgorithmCalculationStatus.SUCCESS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            setStatus(algorithmTask, AlgorithmCalculationStatus.FAILED);
        }
    }

    private void setStatus(AlgorithmTask algorithmTask, AlgorithmCalculationStatus status) {
        algorithmTask.setStatus(status);
    }

}
