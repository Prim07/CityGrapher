package com.agh.bsct.algorithm.controllers;

import com.agh.bsct.algorithm.services.runner.AlgorithmRunnerService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class AlgorithmController {

    private static final String ALGORITHM_PATH = "algorithm/";

    private final AlgorithmRunnerService algorithmRunnerService;

    @Autowired
    public AlgorithmController(AlgorithmRunnerService algorithmRunnerService) {
        this.algorithmRunnerService = algorithmRunnerService;
    }

    @RequestMapping(method = RequestMethod.GET, value = ALGORITHM_PATH + "{id}")
    public String getResults(@PathVariable(value = "id") String id) {
        return "Hello, Algorithm!";
    }

    @RequestMapping(method = RequestMethod.POST, value = ALGORITHM_PATH)
    @ResponseBody
    public String run(@RequestBody ObjectNode graphData) {
        return algorithmRunnerService.run(graphData);
    }

}
