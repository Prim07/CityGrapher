package com.agh.bsct.algorithm.controllers;

import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class AlgorithmController {

    private static final String ALGORITHM_PATH = "algorithm/";

    @RequestMapping(method = RequestMethod.POST, value = ALGORITHM_PATH)
    public String getResults() {
        return "Hello, Algorithm!";
    }

    @RequestMapping(method = RequestMethod.GET, value = ALGORITHM_PATH + "{id}")
    @ResponseBody
    public String run(@PathVariable("id") String id) {
        return "Hello, Algorithm! " + id;
    }

}
