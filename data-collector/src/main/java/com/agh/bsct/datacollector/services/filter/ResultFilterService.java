package com.agh.bsct.datacollector.services.filter;

import com.agh.bsct.datacollector.library.adapter.OverpassQueryResult;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class ResultFilterService {

    public OverpassQueryResult removeAreaTags(OverpassQueryResult queryResult) {
        var iterator = queryResult.elements.iterator();
        while (iterator.hasNext()) {
            var area = iterator.next().tags.area;
            if ("yes".equals(area)) {
                iterator.remove();
            }
        }

        return queryResult;
    }

    public OverpassQueryResult joinRoads(OverpassQueryResult removedAreaTagsQueryResult) {
        //TODO implement
        return new OverpassQueryResult();
    }
}
