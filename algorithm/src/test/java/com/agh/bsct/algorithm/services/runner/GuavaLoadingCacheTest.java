package com.agh.bsct.algorithm.services.runner;

import com.agh.bsct.algorithm.controllers.mapper.GraphDataMapper;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@RunWith(JUnitPlatform.class)
class GuavaLoadingCacheTest {

    private AlgorithmResultCache algorithmResultCache;
    private GraphDataDTO graphDataDTO;

    @BeforeEach
    void setUp() {
        var algorithmTaskRepository = new AlgorithmTaskRepository();
        var graphDataMapper = new GraphDataMapper();
        this.algorithmResultCache = new GuavaLoadingCache(algorithmTaskRepository, graphDataMapper);
        this.graphDataDTO = new GraphDataDTO(Collections.emptyList(), Collections.emptyList());
    }

    @Test
    void shouldCreateSingleTask() {
        AlgorithmTask task = tryCreateNewTask();

        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getId());
    }

    @Test
    void shouldCreateMultipleTasks() {
        String firstTaskId = tryCreateNewTask().getId();
        String secondTaskId = tryCreateNewTask().getId();
        String thirdTaskId = tryCreateNewTask().getId();

        Assert.assertNotNull(firstTaskId);
        Assert.assertNotNull(secondTaskId);
        Assert.assertNotNull(thirdTaskId);
    }

    @Test
    void shouldReturnCreatedTask() {
        String taskId = tryCreateNewTask().getId();
        AlgorithmTask task = tryGetTask(taskId);

        Assert.assertNotNull(task);
        Assert.assertEquals(taskId, task.getId());
    }

    @Test
    void shouldNotCreateNewTaskWhenGetTaskMethodIsCalledMultipleTimes() {
        String taskId = tryCreateNewTask().getId();
        AlgorithmTask taskFromFirstGetAttempt = tryGetTask(taskId);
        AlgorithmTask taskFromSecondGetAttempt = tryGetTask(taskId);
        AlgorithmTask taskFromThirdGetAttempt = tryGetTask(taskId);

        Assert.assertEquals(taskFromFirstGetAttempt, taskFromSecondGetAttempt);
        Assert.assertEquals(taskFromSecondGetAttempt, taskFromThirdGetAttempt);
    }

    private AlgorithmTask tryCreateNewTask() {
        AlgorithmTask task = null;
        try {
            task = algorithmResultCache.createNewTask(graphDataDTO);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return task;
    }

    private AlgorithmTask tryGetTask(String taskId) {
        AlgorithmTask task = null;
        try {
            task = algorithmResultCache.getTask(taskId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return task;
    }
}