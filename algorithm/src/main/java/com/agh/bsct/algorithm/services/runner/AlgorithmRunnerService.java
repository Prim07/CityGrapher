package com.agh.bsct.algorithm.services.runner;

import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.algorithm.services.runner.asyncrunner.AsyncAlgorithmTaskRunner;
import com.agh.bsct.algorithm.services.runner.cache.AlgorithmResultCache;
import com.agh.bsct.algorithm.services.runner.cache.GuavaLoadingCache;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
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

    public String run(GraphDataDTO graphDataDTO) throws ExecutionException {
        AlgorithmTask algorithmTask = algorithmResultCache.createNewTask(graphDataDTO);
        asyncAlgorithmTaskRunner.run(algorithmTask);
        return algorithmTask.getTaskId();
    }

    public AlgorithmTask get(String id) throws ExecutionException {
        return algorithmResultCache.getTask(id);
    }

}
