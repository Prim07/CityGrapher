package com.agh.bsct.algorithm.services.runner;

import com.agh.bsct.api.entities.graphdata.GraphDataDTO;

import java.util.concurrent.ExecutionException;

public interface AlgorithmResultCache {

    AlgorithmTask createNewTask(GraphDataDTO graphDataDTO) throws ExecutionException;

    AlgorithmTask getTask(String id) throws ExecutionException;

}
