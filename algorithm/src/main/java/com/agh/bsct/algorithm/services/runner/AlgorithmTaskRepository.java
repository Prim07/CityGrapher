package com.agh.bsct.algorithm.services.runner;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AlgorithmTaskRepository {

    private final ConcurrentMap<String, AlgorithmTask> idToTask = new ConcurrentHashMap<>();

    AlgorithmTask put(String id, AlgorithmTask algorithmTask) {
        return idToTask.putIfAbsent(id, algorithmTask);
    }

    AlgorithmTask getAlgorithmTaskById(String id) {
        return idToTask.get(id);
    }

    void remove(String id) {
        idToTask.remove(id);
    }

    public void printAllElements() {
        System.out.println("Printing: ");
        idToTask.forEach((key, value) -> System.out.println(key + ": " + value.getId()));
        System.out.println("=============================================================");
    }
}
