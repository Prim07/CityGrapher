package com.agh.bsct.datacollector.controllers;


import com.agh.bsct.datacollector.services.city.OSMCityService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@CrossOrigin
@Controller
public class DataCollectorController {

    private static final String DATA_COLLECTOR_PATH = "/dataCollector";
    private static final String GET_CITY_GRAPH_PATH = "/cityGraph";
    private static final String GET_CITY_DATA_PATH = "/cityData";
    private static final String EXAMPLE_CALL_ALGORITHM_PATH = "/exampleCallAlgorithm";

    private final OSMCityService osmCityService;

    @Autowired
    public DataCollectorController(OSMCityService osmCityService) {
        this.osmCityService = osmCityService;
    }

    @GetMapping(DATA_COLLECTOR_PATH + GET_CITY_GRAPH_PATH)
    @ResponseBody
    public ResponseEntity<ObjectNode> getCityGraph(@RequestParam(name = "city") String city) {
        ObjectNode cityGraph = osmCityService.getCityGraph(city);
        return getSuccessfulResponseWithObjectNode(cityGraph);
    }

    @GetMapping(DATA_COLLECTOR_PATH + GET_CITY_DATA_PATH)
    @ResponseBody
    public ResponseEntity<ObjectNode> getCityData(@RequestParam(name = "city") String city) {
        ObjectNode cityData = osmCityService.getCityData(city);
        return getSuccessfulResponseWithObjectNode(cityData);
    }

    //TODO delete this endpoint or change its path when clear how to call algorithm
    @GetMapping(DATA_COLLECTOR_PATH + EXAMPLE_CALL_ALGORITHM_PATH)
    @ResponseBody
    public ResponseEntity<String> exampleCallAlgorithm(@RequestParam(name = "city") String city) {
        String algorithmData = osmCityService.getAlgorithmData(city);
        return getSuccessfulResponseWithString(algorithmData);
    }

    private ResponseEntity<ObjectNode> getSuccessfulResponseWithObjectNode(ObjectNode cityData) {
        return ResponseEntity.status(HttpStatus.OK).body(cityData);
    }

    private ResponseEntity<String> getSuccessfulResponseWithString(String algorithmData) {
        return ResponseEntity.status(HttpStatus.OK).body(algorithmData);
    }

}
