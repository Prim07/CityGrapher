package com.agh.bsct.algorithm.controllers;

import com.agh.bsct.algorithm.controllers.config.PathsConstants;
import com.agh.bsct.algorithm.controllers.mapper.AlgorithmTaskMapper;
import com.agh.bsct.algorithm.services.runner.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.AlgorithmRunnerService;
import com.agh.bsct.algorithm.services.runner.AlgorithmTask;
import com.agh.bsct.api.entities.algorithmresult.AlgorithmResultDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.CacheLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@CrossOrigin
@RestController
public class AlgorithmController {

    private static final String ALGORITHM_PATH = "algorithm/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AlgorithmRunnerService algorithmRunnerService;
    private AlgorithmTaskMapper algorithmTaskMapper;

    @Autowired
    public AlgorithmController(AlgorithmRunnerService algorithmRunnerService, AlgorithmTaskMapper algorithmTaskMapper) {
        this.algorithmRunnerService = algorithmRunnerService;
        this.algorithmTaskMapper = algorithmTaskMapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = ALGORITHM_PATH + "{taskId}")
    public ResponseEntity<AlgorithmResultDTO> getResults(@PathVariable String taskId) {
        try {
            AlgorithmTask task = algorithmRunnerService.get(taskId);
            return (task.getStatus() == AlgorithmCalculationStatus.SUCCESS)
                    ? getSuccessfulResponseWithAlgorithmTask(task)
                    : getAcceptedResponseWithAlgorithmTask(task);
        } catch (CacheLoader.InvalidCacheLoadException e) {
            e.printStackTrace();
            return getNotFoundResponse(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return getFailureResponseWithAlgorithmResultDTO(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = ALGORITHM_PATH)
    @ResponseBody
    public ResponseEntity<ObjectNode> run(@RequestBody GraphDataDTO graphDataDTO) {
        try {
            String taskId = algorithmRunnerService.run(graphDataDTO);
            return getSuccessfulResponseWithUriToTask(taskId);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return getFailureResponse(e);
        }
    }

    private ResponseEntity<AlgorithmResultDTO> getSuccessfulResponseWithAlgorithmTask(AlgorithmTask task) {
        AlgorithmResultDTO algorithmResultDTO = algorithmTaskMapper.mapToAlgorithmResultDTO(task);
        return ResponseEntity.status(HttpStatus.OK).body(algorithmResultDTO);
    }

    private ResponseEntity<AlgorithmResultDTO> getAcceptedResponseWithAlgorithmTask(AlgorithmTask task) {
        AlgorithmResultDTO algorithmResultDTO = algorithmTaskMapper.mapToAlgorithmResultDTO(task);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(algorithmResultDTO);
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

    private ResponseEntity<AlgorithmResultDTO> getNotFoundResponse(CacheLoader.InvalidCacheLoadException e) {
        AlgorithmResultDTO algorithmResultDTO = getAlgorithmResultWithErrorStatus(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(algorithmResultDTO);
    }

    private ResponseEntity<AlgorithmResultDTO> getFailureResponseWithAlgorithmResultDTO(ExecutionException e) {
        AlgorithmResultDTO algorithmResultDTO = getAlgorithmResultWithErrorStatus(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(algorithmResultDTO);
    }

    private AlgorithmResultDTO getAlgorithmResultWithErrorStatus(String message) {
        return AlgorithmResultDTO.builder()
                .taskId("empty")
                .status("Error: " + message)
                .graphData(null)
                .hospitals(Collections.emptyList())
                .build();
    }

}
