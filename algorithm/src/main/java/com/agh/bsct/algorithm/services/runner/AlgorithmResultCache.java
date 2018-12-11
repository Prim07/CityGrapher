package com.agh.bsct.algorithm.services.runner;

import com.agh.bsct.algorithm.entities.graph.Graph;

import java.util.concurrent.ExecutionException;

public interface AlgorithmResultCache {

    AlgorithmTask createNewTask(Graph graph) throws ExecutionException;

    AlgorithmTask getTask(String id) throws ExecutionException;

}
