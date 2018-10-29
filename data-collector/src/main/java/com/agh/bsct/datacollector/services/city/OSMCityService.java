package com.agh.bsct.datacollector.services.city;

import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import com.agh.bsct.datacollector.services.filter.ResultFilterService;
import com.agh.bsct.datacollector.services.graph.GraphService;
import com.agh.bsct.datacollector.services.interpreter.QueryInterpreterService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;

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
        HashMap<String, LinkedList<Long>> streetNameToNodes = resultFilterService.joinRoads(removedAreaTagsQueryResult);
        return graphService.createGraph(streetNameToNodes);
    }

}
