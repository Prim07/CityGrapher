package com.agh.bsct.algorithm.services.runner;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.concurrent.ExecutionException;

public interface AlgorithmResultCache {

    AlgorithmTask createNewTask(ObjectNode graphData) throws ExecutionException;

    AlgorithmTask getTask(String id) throws ExecutionException;

}
