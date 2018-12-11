package com.agh.bsct.algorithm.services.runner;

import com.agh.bsct.algorithm.entities.graph.Graph;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GuavaLoadingCache implements AlgorithmResultCache {

    private AlgorithmTaskRepository algorithmTaskRepository;
    private LoadingCache<String, AlgorithmTask> idToTaskCache;

    @Autowired
    public GuavaLoadingCache(AlgorithmTaskRepository algorithmTaskRepository) {
        this.algorithmTaskRepository = algorithmTaskRepository;
        this.idToTaskCache = getInitializedLoadingCache();
    }

    @Override
    public AlgorithmTask createNewTask(Graph graph) {
        String id = UUID.randomUUID().toString();
        AlgorithmTask algorithmTask = new AlgorithmTask(id, graph);
        algorithmTaskRepository.put(id, algorithmTask);
        return algorithmTask;
    }

    @Override
    public AlgorithmTask getTask(String id) throws ExecutionException {
        return idToTaskCache.get(id);
    }

    private LoadingCache<String, AlgorithmTask> getInitializedLoadingCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .removalListener(getRemovalListener())
                .build(getCacheLoader());
    }

    private RemovalListener<String, AlgorithmTask> getRemovalListener() {
        return removalNotification -> algorithmTaskRepository.remove(removalNotification.getKey());
    }

    private CacheLoader<String, AlgorithmTask> getCacheLoader() {
        return new CacheLoader<>() {
            @Override
            public AlgorithmTask load(@NonNull String id) {
                return algorithmTaskRepository.getAlgorithmTaskById(id);
            }
        };
    }
}
