package com.agh.bsct.datacollector.services.city;

import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import com.agh.bsct.datacollector.services.algorithm.boundary.AlgorithmService;
import com.agh.bsct.datacollector.services.data.CityDataService;
import com.agh.bsct.datacollector.services.data.GraphDataService;
import com.agh.bsct.datacollector.services.parser.DataParser;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class OSMCityService {

    private GraphDataService graphService;
    private CityDataService cityDataService;
    private AlgorithmService algorithmService;
    private DataParser dataParser;

    @Autowired
    public OSMCityService(GraphDataService graphService,
                          CityDataService cityDataService,
                          AlgorithmService algorithmService,
                          DataParser dataParser) {
        this.graphService = graphService;
        this.cityDataService = cityDataService;
        this.algorithmService = algorithmService;
        this.dataParser = dataParser;
    }

    public ObjectNode getCityData(String cityName) {
        var cityDataDTO = cityDataService.getCityDataDTO(cityName);
        return dataParser.parseToJson(cityDataDTO);
    }

    public ObjectNode getCityGraph(String cityName) {
        var cityDataDTO = cityDataService.getCityDataDTO(cityName);
        var graphDataDTO = graphService.getGraphDataDTO(cityDataDTO);
        return algorithmService.run(graphDataDTO);
        /* TODO poniższa część kodu kiedyś miała sens, ale teraz musi przejść w nowe miejsce, jeszcze nie do koca wiadomo, gdzie
        var graph = new Graph(graphDataDTO);
        var hospitalNodes = graphService.runAlgorithmAndCalculateHospitalNodes(jsonGraph);
        return dataParser.parseToJson(graphDataDTO, hospitalNodes); */
    }

    //TODO AK same as in TODO comment above exampleCallAlgorithm method in DataCollectorController
    public ObjectNode getAlgorithmData() {
        return algorithmService.run(new GraphDataDTO(Collections.emptyList(), Collections.emptyList()));
    }

    public ObjectNode getMappedAlgorithmResult(String taskId) {
        ObjectNode result = algorithmService.getResult(taskId);
        return result;
    }
}
