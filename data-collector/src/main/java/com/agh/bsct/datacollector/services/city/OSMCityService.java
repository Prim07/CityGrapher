package com.agh.bsct.datacollector.services.city;

import com.agh.bsct.datacollector.library.adapter.OverpassQueryResult;
import com.agh.bsct.datacollector.services.filter.ResultFilterService;
import com.agh.bsct.datacollector.services.graph.GraphService;
import com.agh.bsct.datacollector.services.interpreter.QueryInterpreterService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OSMCityService {

    private QueryForCityProvider queryForCityProvider;
    private QueryInterpreterService queryInterpreterService;
    private ResultFilterService resultFilterService;
    private GraphService graphService;

    @Autowired
    public OSMCityService(QueryForCityProvider queryForCityProvider, QueryInterpreterService queryInterpreterService,
                          ResultFilterService resultFilterService, GraphService graphService) {
        this.queryForCityProvider = queryForCityProvider;
        this.queryInterpreterService = queryInterpreterService;
        this.resultFilterService = resultFilterService;
        this.graphService = graphService;
    }

    public ObjectNode getCityData(String cityName) {
        String query = queryForCityProvider.getQueryForCity(cityName);
        OverpassQueryResult interpretedQuery = queryInterpreterService.interpret(query);
        OverpassQueryResult removedAreaTagsQueryResult = resultFilterService.removeAreaTags(interpretedQuery);
        OverpassQueryResult joinedRoadsQueryResults = resultFilterService.joinRoads(removedAreaTagsQueryResult);
        return graphService.createGraph(joinedRoadsQueryResults);
    }

}
