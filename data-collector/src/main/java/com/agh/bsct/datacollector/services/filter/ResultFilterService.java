package com.agh.bsct.datacollector.services.filter;

import com.agh.bsct.datacollector.library.adapter.OverpassQueryResult;
import org.springframework.stereotype.Service;

@Service
public class ResultFilterService {

    public OverpassQueryResult removeAreaTags(OverpassQueryResult queryResult) {
        //TODO implement
        return new OverpassQueryResult();
    }

    public OverpassQueryResult joinRoads(OverpassQueryResult removedAreaTagsQueryResult) {
        //TODO implement
        return new OverpassQueryResult();
    }
}
