package com.agh.bsct.datacollector.services.filter;

import com.agh.bsct.datacollector.library.adapter.OverpassQueryResult;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class ResultFilterService {

    public OverpassQueryResult removeAreaTags(OverpassQueryResult queryResult) {
        Iterator<OverpassQueryResult.Element> iterator = queryResult.elements.iterator();
        while (iterator.hasNext()) {
            String area = iterator.next().tags.area;
            if (area != null && area.equals("yes")) {
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
