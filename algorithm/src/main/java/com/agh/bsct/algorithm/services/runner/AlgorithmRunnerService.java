package com.agh.bsct.algorithm.services.runner;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class AlgorithmRunnerService {

    private AlgorithmResultCache algorithmResultCache;
    private AsyncAlgorithmTaskRunner asyncAlgorithmTaskRunner;

    @Autowired
    public AlgorithmRunnerService(GuavaLoadingCache guavaLoadingCache,
                                  AsyncAlgorithmTaskRunner asyncAlgorithmTaskRunner) {
        this.algorithmResultCache = guavaLoadingCache;
        this.asyncAlgorithmTaskRunner = asyncAlgorithmTaskRunner;
    }

    public String run(ObjectNode graphData) throws ExecutionException {
        AlgorithmTask algorithmTask = algorithmResultCache.createNewTask(graphData);
        asyncAlgorithmTaskRunner.run(algorithmTask);
        return algorithmTask.getId();
    }

    public AlgorithmTask get(String id) throws ExecutionException {
        return algorithmResultCache.getTask(id);
    }

}
