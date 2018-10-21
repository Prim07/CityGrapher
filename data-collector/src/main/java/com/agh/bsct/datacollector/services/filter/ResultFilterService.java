package com.agh.bsct.datacollector.services.filter;

import com.agh.bsct.datacollector.library.adapter.queryresult.Element;
import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultFilterService {

    public OverpassQueryResult removeAreaTags(OverpassQueryResult queryResult) {
        var iterator = queryResult.getElements().iterator();
        while (iterator.hasNext()) {
            var area = iterator.next().getTags().getArea();
            if ("yes".equals(area)) {
                iterator.remove();
            }
        }

        return queryResult;
    }

    public OverpassQueryResult joinRoads(OverpassQueryResult removedAreaTagsQueryResult) {
        List<Element> elements = removedAreaTagsQueryResult.getElements().stream()
                .filter(element -> element.getType().equals("way"))
                .collect(Collectors.toList());
        return new OverpassQueryResult();
    }
}
