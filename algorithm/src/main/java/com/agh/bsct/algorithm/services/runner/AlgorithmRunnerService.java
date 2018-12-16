package com.agh.bsct.algorithm.services.runner;

import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.algorithm.services.runner.asyncrunner.AsyncAlgorithmTaskRunner;
import com.agh.bsct.algorithm.services.runner.cache.AlgorithmResultCache;
import com.agh.bsct.algorithm.services.runner.cache.GuavaLoadingCache;
import com.agh.bsct.algorithm.services.runner.repository.AsyncTaskRepository;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class AlgorithmRunnerService {

    private AlgorithmResultCache algorithmResultCache;
    private AsyncAlgorithmTaskRunner asyncAlgorithmTaskRunner;
    private AsyncTaskRepository asyncTaskRepository;

    @Autowired
    public AlgorithmRunnerService(GuavaLoadingCache guavaLoadingCache,
                                  AsyncAlgorithmTaskRunner asyncAlgorithmTaskRunner,
                                  AsyncTaskRepository asyncTaskRepository) {
        this.algorithmResultCache = guavaLoadingCache;
        this.asyncAlgorithmTaskRunner = asyncAlgorithmTaskRunner;
        this.asyncTaskRepository = asyncTaskRepository;
    }

    public String run(GraphDataDTO graphDataDTO) throws ExecutionException {
        AlgorithmTask algorithmTask = algorithmResultCache.createNewTask(graphDataDTO);
        Future<Integer> asyncTask = asyncAlgorithmTaskRunner.run(algorithmTask);
        asyncTaskRepository.put(algorithmTask.getTaskId(), asyncTask);
        return algorithmTask.getTaskId();
    }

    public AlgorithmTask get(String id) throws ExecutionException {
        return algorithmResultCache.getTask(id);
    }

    public void cancel(String id) {
        Future asyncTaskById = asyncTaskRepository.getAsyncTaskById(id);
        asyncTaskById.cancel(true);
        asyncTaskRepository.remove(id);
    }
}
