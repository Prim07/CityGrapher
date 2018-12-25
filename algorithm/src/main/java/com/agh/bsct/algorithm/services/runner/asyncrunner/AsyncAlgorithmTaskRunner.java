package com.agh.bsct.algorithm.services.runner.asyncrunner;

import com.agh.bsct.algorithm.Algorithm;
import com.agh.bsct.algorithm.algorithms.factory.IAlgorithmFactory;
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

    private IAlgorithmFactory iAlgorithmFactory;

    @Autowired
    public AsyncAlgorithmTaskRunner(IAlgorithmFactory iAlgorithmFactory) {
        this.iAlgorithmFactory = iAlgorithmFactory;
    }

    @Async(Algorithm.SPRING_THREAD_POOL_NAME)
    public Future<Integer> run(AlgorithmTask algorithmTask) {
        var algorithm = iAlgorithmFactory.getIAlgorithm(algorithmTask.getAlgorithmType());
        algorithm.run(algorithmTask);
        return new AsyncResult<>(THREAD_COUNT.getAndIncrement());
    }


}
