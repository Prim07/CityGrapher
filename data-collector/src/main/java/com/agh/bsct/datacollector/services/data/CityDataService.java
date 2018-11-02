package com.agh.bsct.datacollector.services.data;

import com.agh.bsct.datacollector.entities.citydata.CityData;
import com.agh.bsct.datacollector.entities.citydata.Street;
import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import com.agh.bsct.datacollector.services.parser.DataParser;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CityDataService {

    private DataParser dataParser;

    @Autowired
    public CityDataService(DataParser dataParser) {
        this.dataParser = dataParser;
    }

    public CityData buildCityData(OverpassQueryResult removedAreaTagsQueryResult, Set<Street> streets) {
        return new CityData();
        //TODO impl
    }

    public CityData findCrossings(CityData cityData) {

        return new CityData();
        //TODO impl
    }

    public ObjectNode returnCityDataAsObjectNode(CityData cityData) {
        //TODO impl
        return dataParser.parseToJson(cityData);
    }
}
