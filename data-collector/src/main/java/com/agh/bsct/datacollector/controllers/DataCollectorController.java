package com.agh.bsct.datacollector.controllers;


import com.agh.bsct.datacollector.services.city.OSMCityService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
public class DataCollectorController {

    private static final String DATA_COLLECTOR_PATH = "/dataCollector";
    private static final String GET_CITY_GRAPH_PATH = "/cityGraph";
    private static final String GET_CITY_DATA_PATH = "/cityData";
    private static final String GET_ALGORITHM_RESULT_PATH = "/algorithmResult";

    private final OSMCityService osmCityService;

    @Autowired
    public DataCollectorController(OSMCityService osmCityService) {
        this.osmCityService = osmCityService;
    }

    @GetMapping(DATA_COLLECTOR_PATH + GET_CITY_GRAPH_PATH)
    @ResponseBody
    public ObjectNode getCityGraph(@RequestParam(name = "city") String city) {
        return osmCityService.getCityGraph(city);
    }

    @GetMapping(DATA_COLLECTOR_PATH + GET_CITY_DATA_PATH)
    @ResponseBody
    public ObjectNode getCityData(@RequestParam(name = "city") String city) {
        return osmCityService.getCityData(city);
    }

    @GetMapping(DATA_COLLECTOR_PATH + GET_ALGORITHM_RESULT_PATH)
    @ResponseBody
    public ObjectNode getMappedAlgorithmResult(@PathVariable String taskId) {
        return osmCityService.getMappedAlgorithmResult(taskId);
    }

}
