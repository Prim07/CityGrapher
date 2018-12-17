package com.agh.bsct.datacollector.services.city;

import com.agh.bsct.api.entities.algorithmorder.AlgorithmOrderDTO;
import com.agh.bsct.datacollector.services.algorithm.boundary.AlgorithmService;
import com.agh.bsct.datacollector.services.data.CityDataService;
import com.agh.bsct.datacollector.services.data.GraphDataService;
import com.agh.bsct.datacollector.services.parser.DataParser;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public ObjectNode getCityGraph(String cityName, Integer numberOfResults) {
        var cityDataDTO = cityDataService.getCityDataDTO(cityName);
        var graphDataDTO = graphService.getGraphDataDTO(cityDataDTO);
        var algorithmOrderDTO = new AlgorithmOrderDTO(numberOfResults, graphDataDTO);
        return algorithmService.run(algorithmOrderDTO);
    }

    public ObjectNode getMappedAlgorithmResult(String taskId) {
        var result = algorithmService.getResult(taskId);
        return dataParser.parseToJson(result.getGraphData(), result.getHospitals());
    }
}
