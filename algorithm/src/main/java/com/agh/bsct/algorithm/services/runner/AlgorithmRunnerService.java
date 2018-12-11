package com.agh.bsct.algorithm.services.runner;

import com.agh.bsct.algorithm.entities.graph.Graph;
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

    public String run(Graph graph) throws ExecutionException {
        AlgorithmTask algorithmTask = algorithmResultCache.createNewTask(graph);
        asyncAlgorithmTaskRunner.run(algorithmTask);
        return algorithmTask.getId();
    }

    public AlgorithmTask get(String id) throws ExecutionException {
        return algorithmResultCache.getTask(id);
    }

}
