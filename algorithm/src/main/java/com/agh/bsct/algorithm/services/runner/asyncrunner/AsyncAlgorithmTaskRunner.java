package com.agh.bsct.algorithm.services.runner.asyncrunner;

import com.agh.bsct.algorithm.Algorithm;
import com.agh.bsct.algorithm.algorithms.IAlgorithm;
import com.agh.bsct.algorithm.algorithms.SAAlgorithm;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AsyncAlgorithmTaskRunner {

    private static AtomicInteger THREAD_COUNT = new AtomicInteger(0);

    private IAlgorithm algorithm;

    @Autowired
    public AsyncAlgorithmTaskRunner(@Qualifier(SAAlgorithm.SIMULATED_ANNEALING_QUALIFIER) IAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Async(Algorithm.SPRING_THREAD_POOL_NAME)
    public Future<Integer> run(AlgorithmTask algorithmTask) {
        int currentThreadNumber = THREAD_COUNT.getAndIncrement();

        //TODO implement here calculating and logic and remove below fake log calculations
        try {
            algorithm.run(algorithmTask);
        } catch (InterruptedException e) {
            System.out.println("Interrupted thread: " + currentThreadNumber);
            Thread.currentThread().interrupt();
            algorithmTask.setStatus(AlgorithmCalculationStatus.CANCELLED);
        }

        return new AsyncResult<>(currentThreadNumber);
    }


}
