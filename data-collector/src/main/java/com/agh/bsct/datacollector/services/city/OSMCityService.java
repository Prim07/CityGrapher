package com.agh.bsct.datacollector.services.city;

import com.agh.bsct.datacollector.entities.citydata.CityData;
import com.agh.bsct.datacollector.entities.citydata.Street;
import com.agh.bsct.datacollector.entities.graphdata.GraphData;
import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import com.agh.bsct.datacollector.services.data.CityDataService;
import com.agh.bsct.datacollector.services.filter.ResultFilterService;
import com.agh.bsct.datacollector.services.data.GraphDataService;
import com.agh.bsct.datacollector.services.interpreter.QueryInterpreterService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OSMCityService {

    private QueryForCityProvider queryForCityProvider;
    private QueryInterpreterService queryInterpreterService;
    private ResultFilterService resultFilterService;
    private GraphDataService graphService;
    private CityDataService cityDataService;

    @Autowired
    public OSMCityService(QueryForCityProvider queryForCityProvider, QueryInterpreterService queryInterpreterService,
                          ResultFilterService resultFilterService, GraphDataService graphService,
                          CityDataService cityDataService) {
        this.queryForCityProvider = queryForCityProvider;
        this.queryInterpreterService = queryInterpreterService;
        this.resultFilterService = resultFilterService;
        this.graphService = graphService;
        this.cityDataService = cityDataService;
    }

    public ObjectNode getCityData(String cityName) {
        String query = queryForCityProvider.getQueryForCity(cityName);

        OverpassQueryResult interpretedQuery = queryInterpreterService.interpret(query);
        OverpassQueryResult removedAreaTagsQueryResult = resultFilterService.removeAreaTags(interpretedQuery);

        Set<Street> streets = resultFilterService.joinRoads(removedAreaTagsQueryResult);

        CityData cityData = cityDataService.buildCityData(removedAreaTagsQueryResult, streets);
        CityData cityDataWithCrossing = cityDataService.findCrossings(cityData);

        return cityDataService.returnCityDataAsObjectNode(cityDataWithCrossing);
    }

    public ObjectNode getCityGraph(String cityName) {
        String query = queryForCityProvider.getQueryForCity(cityName);

        OverpassQueryResult interpretedQuery = queryInterpreterService.interpret(query);
        OverpassQueryResult removedAreaTagsQueryResult = resultFilterService.removeAreaTags(interpretedQuery);

        Set<Street> streets = resultFilterService.joinRoads(removedAreaTagsQueryResult);

        CityData cityData = cityDataService.buildCityData(removedAreaTagsQueryResult, streets);
        CityData cityDataWithCrossing = cityDataService.findCrossings(cityData);

        GraphData graphData = graphService.buildGraphData(cityDataWithCrossing);
        GraphData graphDataWithEdgeWeights = graphService.calculateEdgeWeights(graphData);
        GraphData graphDataWithNodeWeights = graphService.calculateNodeWeights(graphDataWithEdgeWeights);

        return graphService.returnGraphAsObjectNode(graphDataWithNodeWeights);
    }

}
