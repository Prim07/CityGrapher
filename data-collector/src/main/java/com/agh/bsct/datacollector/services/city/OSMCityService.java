package com.agh.bsct.datacollector.services.city;

import com.agh.bsct.datacollector.entities.graph.Graph;
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
    private DataParser dataParser;

    @Autowired
    public OSMCityService(GraphDataService graphService, CityDataService cityDataService, DataParser dataParser) {
        this.graphService = graphService;
        this.cityDataService = cityDataService;
        this.dataParser = dataParser;
    }

    public ObjectNode getCityData(String cityName) {
        var cityData = cityDataService.getCityData(cityName);
        return dataParser.parseToJson(cityData);
    }

    public ObjectNode getCityGraph(String cityName) {
        var cityData = cityDataService.getCityData(cityName);
        var graphData = graphService.getGraphData(cityData);
        var parsedGraph = dataParser.parseToJson(new Graph(graphData));
        //TODO AK wystawić tutaj algorithmService, który będzie się łączył z modułem Algorithm i koniecznie zmienić nazwę metody
        var hospitalNodes = graphService.runAlgorithmAndCalculateHospitalNodes(parsedGraph);
        return dataParser.parseToJson(graphData, hospitalNodes);
    }

}
