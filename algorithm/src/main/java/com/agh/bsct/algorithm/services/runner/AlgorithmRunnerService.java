package com.agh.bsct.algorithm.services.runner;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AlgorithmRunnerService {

    private LoadingCache<String, AlgorithmResult> idToResultCache;

    public String run(ObjectNode graphData) {
        String uuid = UUID.randomUUID().toString();
        //TODO AK create first LoadingCache and then start new task
        return uuid;
    }
}
