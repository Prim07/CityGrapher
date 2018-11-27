package com.agh.bsct.datacollector.services.city;

import com.agh.bsct.datacollector.entities.graph.Graph;
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
        var cityData = cityDataService.getCityData(cityName);
        return dataParser.parseToJson(cityData);
    }

    public ObjectNode getCityGraph(String cityName) {
        var cityData = cityDataService.getCityData(cityName);
        var graphData = graphService.getGraphData(cityData);
        var jsonGraph = dataParser.parseToJson(new Graph(graphData));
        //TODO AK wystawić tutaj algorithmService, który będzie się łączył z modułem Algorithm i koniecznie zmienić nazwę metody
        var hospitalNodes = graphService.runAlgorithmAndCalculateHospitalNodes(jsonGraph);
        return dataParser.parseToJson(graphData, hospitalNodes);
    }

    //TODO AK same as in TODO comment above exampleCallAlgorithm method in DataCollectorController
    public String getAlgorithmData(String city) {
        return algorithmService.run(city);
    }
}
