package com.agh.bsct.algorithm.controllers;

import com.agh.bsct.algorithm.controllers.config.PathsConstants;
import com.agh.bsct.algorithm.services.runner.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.AlgorithmRunnerService;
import com.agh.bsct.algorithm.services.runner.AlgorithmTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.CacheLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@CrossOrigin
@RestController
public class AlgorithmController {

    private static final String ALGORITHM_PATH = "algorithm/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AlgorithmRunnerService algorithmRunnerService;

    @Autowired
    public AlgorithmController(AlgorithmRunnerService algorithmRunnerService) {
        this.algorithmRunnerService = algorithmRunnerService;
    }

    @RequestMapping(method = RequestMethod.GET, value = ALGORITHM_PATH + "{id}")
    public ResponseEntity<ObjectNode> getResults(@PathVariable String id) {
        try {
            AlgorithmTask task = algorithmRunnerService.get(id);
            return (task.getStatus() == AlgorithmCalculationStatus.SUCCESS)
                    ? getSuccessfulResponseWithAlgorithmTask(task)
                    : getAcceptedResponseWithAlgorithmTask(task);
        } catch (CacheLoader.InvalidCacheLoadException e) {
            e.printStackTrace();
            return getNotFoundResponse(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return getFailureResponse(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = ALGORITHM_PATH)
    @ResponseBody
    public ResponseEntity<ObjectNode> run(@RequestBody ObjectNode graphData) {
        try {
            String taskId = algorithmRunnerService.run(graphData);
            return getSuccessfulResponseWithUriToTask(taskId);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return getFailureResponse(e);
        }
    }

    private ResponseEntity<ObjectNode> getSuccessfulResponseWithAlgorithmTask(AlgorithmTask task) {
        ObjectNode responseJson = mapToResponseJson(task);
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }

    private ResponseEntity<ObjectNode> getAcceptedResponseWithAlgorithmTask(AlgorithmTask task) {
        ObjectNode responseJson = mapToResponseJson(task);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseJson);
    }

    private ObjectNode mapToResponseJson(AlgorithmTask task) {
        return objectMapper.createObjectNode()
                .put("taskId", task.getId())
                .put("currentStatus", task.getStatus().toString())
                .putPOJO("result", task.getJsonResult().isPresent()
                        ? task.getJsonResult().get()
                        : objectMapper.createObjectNode());
    }

    private ResponseEntity<ObjectNode> getSuccessfulResponseWithUriToTask(String taskId) {
        ObjectNode json = objectMapper.createObjectNode()
                .put("uri", PathsConstants.ROOT_PATH + ALGORITHM_PATH + taskId);
        return ResponseEntity.status(HttpStatus.OK).body(json);
    }

    private ResponseEntity<ObjectNode> getFailureResponse(ExecutionException e) {
        ObjectNode errorJson = objectMapper.createObjectNode()
                .put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorJson);
    }

    private ResponseEntity<ObjectNode> getNotFoundResponse(CacheLoader.InvalidCacheLoadException e) {
        ObjectNode json = objectMapper.createObjectNode()
                .put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(json);
    }

}
