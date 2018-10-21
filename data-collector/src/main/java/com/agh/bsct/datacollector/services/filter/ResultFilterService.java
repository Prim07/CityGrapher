package com.agh.bsct.datacollector.services.filter;

import com.agh.bsct.datacollector.library.adapter.queryresult.Element;
import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

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

    public OverpassQueryResult joinRoads(OverpassQueryResult queryResult) {
        var streetNameToListOfNodes = getStreetNameToListOfNodes(queryResult);

        var streetNameToNodes = new HashMap<String, LinkedHashSet<Long>>();
        var iterator = streetNameToListOfNodes.entrySet().iterator();
        while (iterator.hasNext()) {
            var currentEntry = iterator.next();
            List<List<Long>> listOfNodes = currentEntry.getValue();
            if (listOfNodes.size() == 0) {
                continue;
            }

            List<Long> nodes = new ArrayList<>();

            var mergedNodes = new LinkedHashSet<Long>();
            for (List<Long> currentNodes : listOfNodes) {
                if (currentNodes.size() == 0) {
                    continue;
                }
                Long lastNode = currentNodes.get(currentNodes.size() - 1);

                //search if there is first node in other
                for (List<Long> currentNodesInside : listOfNodes) {
                    if (currentNodes != currentNodesInside) {
                        Long firstNodeInside = currentNodesInside.get(0);
                        if (lastNode.equals(firstNodeInside)) {
                            //merge both lists
                            mergedNodes.addAll(currentNodes);
                            mergedNodes.addAll(currentNodesInside);
                        }
                    }
                }
            }
            String streetName = currentEntry.getKey();
            streetNameToNodes.put(streetName, mergedNodes);

        }
        return new OverpassQueryResult();
    }

    private HashMap<String, List<List<Long>>> getStreetNameToListOfNodes(OverpassQueryResult queryResult) {
        var iterator = queryResult.getElements().iterator();
        var streetNameToNodes = new HashMap<String, List<List<Long>>>();
        while (iterator.hasNext()) {
            Element currentElement = iterator.next();
            var name = currentElement.getTags().getName();
            if (name != null) {
                if (!streetNameToNodes.containsKey(name)) {
                    streetNameToNodes.put(name, new ArrayList<>());
                }
                var nodes = currentElement.getNodesAsArrayList();
                if (nodes != null) {
                    List<List<Long>> listOfStreetNodes = streetNameToNodes.get(name);
                    listOfStreetNodes.add(nodes);
                }
            }
        }
        return streetNameToNodes;
    }
}
