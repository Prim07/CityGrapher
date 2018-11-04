package com.agh.bsct.algorithm.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class AlgorithmController {

    private static final String ALGORITHM_PATH = "algorithm/";

    @RequestMapping(method = RequestMethod.GET, value = ALGORITHM_PATH)
    public String getResults() {
        return "Hello, Algorithm!";
    }

}
