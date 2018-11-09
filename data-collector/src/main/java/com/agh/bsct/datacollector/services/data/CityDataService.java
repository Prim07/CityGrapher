package com.agh.bsct.datacollector.services.data;

import com.agh.bsct.datacollector.entities.citydata.CityData;
import com.agh.bsct.datacollector.entities.citydata.Node;
import com.agh.bsct.datacollector.entities.citydata.Street;
import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import com.agh.bsct.datacollector.services.city.QueryForCityProvider;
import com.agh.bsct.datacollector.services.interpreter.QueryInterpreterService;
import com.agh.bsct.datacollector.services.result.filter.ResultFilterService;
import com.agh.bsct.datacollector.services.result.joiner.StreetsJoinerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CityDataService {

    private static final String NODE_TYPE = "node";

    private QueryForCityProvider queryForCityProvider;
    private QueryInterpreterService queryInterpreterService;
    private ResultFilterService resultFilterService;
    private StreetsJoinerService streetsJoinerService;

    @Autowired
    public CityDataService(QueryForCityProvider queryForCityProvider,
                           QueryInterpreterService queryInterpreterService,
                           ResultFilterService resultFilterService,
                           StreetsJoinerService streetsJoinerService) {
        this.queryForCityProvider = queryForCityProvider;
        this.queryInterpreterService = queryInterpreterService;
        this.resultFilterService = resultFilterService;
        this.streetsJoinerService = streetsJoinerService;
    }

    public CityData getCityData(String cityName) {
        String query = queryForCityProvider.getQueryForCity(cityName);

        OverpassQueryResult interpretedQuery = queryInterpreterService.interpret(query);
        OverpassQueryResult removedAreaTagsQueryResult = resultFilterService.removeAreaTags(interpretedQuery);

        Set<Street> streets = streetsJoinerService.joinStreets(removedAreaTagsQueryResult);

        return updateCrossings(streets, removedAreaTagsQueryResult);
    }

    private CityData updateCrossings(Set<Street> streets, OverpassQueryResult overpassQueryResult) {

        Map<Long, Integer> nodeIdToOccurrencesInStreetCount = getNodeIdToOccurrencesInStreetCountMap(streets);

        List<Long> crossingsIds = getCrossingIds(nodeIdToOccurrencesInStreetCount);

        List<Street> streetsAfterSplitting = getStreetsSeparatedOnCrossings(streets, crossingsIds);

        //prepare list of all nodes (for CityData object)
        List<Node> nodes = overpassQueryResult.getElements().stream()
                .filter(element -> NODE_TYPE.equals(element.getType()))
                .map(element -> new Node(element.getId(), element.getLon(), element.getLat()))
                .collect(Collectors.toList());

        //prepare list of all crossings (for CityData object)
        List<Node> crossing = crossingsIds.stream()
                .map(id -> nodes
                        .stream()
                        .filter(node -> id.equals(node.getId()))
                        .collect(Collectors.toList())
                        .get(0))
                .collect(Collectors.toList());

        return new CityData(nodes, streetsAfterSplitting, crossing);
    }

    private List<Street> getStreetsSeparatedOnCrossings(Set<Street> streets, List<Long> crossingsIds) {
        var streetsSeparatedOnCrossings = new ArrayList<Street>();

        for (Street street : streets) {
            List<Long> nodesIds = street.getNodesIds();
            var crossingWithinStreet = new ArrayList<Long>();
            for (int i = 1; i < nodesIds.size() - 1; i++) {
                Long nodeId = nodesIds.get(i);
                if (crossingsIds.contains(nodeId)) {
                    crossingWithinStreet.add(nodeId);
                }
            }
            if (crossingWithinStreet.size() == 0) {
                streetsSeparatedOnCrossings.add(street);
            } else {
                streetsSeparatedOnCrossings.addAll(splitStreet(street, crossingWithinStreet));
            }
        }

        return streetsSeparatedOnCrossings;
    }

    private List<Long> getCrossingIds(Map<Long, Integer> nodeIdToOccurrencesInStreetCount) {
        return nodeIdToOccurrencesInStreetCount.entrySet().stream()
                .filter(this::isNodesOccurrenceGreaterThanOne)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        //TODO remove commented section if above is 100% working
        /*
        // find crossing ids (nodes with occurrences greater than 1)
        List<Long> crossingsIds = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : nodeIdToOccurrencesInStreetCount.entrySet()) {
            Integer occurrencesInStreets = entry.getValue();
            Long nodeId = entry.getKey();
            if (occurrencesInStreets > 1)
                crossingsIds.add(nodeId);
        }
        return crossingsIds;*/
    }

    private boolean isNodesOccurrenceGreaterThanOne(Map.Entry<Long, Integer> entry) {
        return entry.getValue() > 1;
    }

    private Map<Long, Integer> getNodeIdToOccurrencesInStreetCountMap(Set<Street> streets) {
        Map<Long, Integer> nodeIdsToOccurrencesInStreets = new HashMap<>();
        streets.stream()
                .map(Street::getNodesIds)
                .flatMap(Collection::stream)
                .forEach(nodeId -> nodeIdsToOccurrencesInStreets.merge(nodeId, 1, (a, b) -> a + b));
        return nodeIdsToOccurrencesInStreets;
    }

    private List<Street> splitStreet(Street street, List<Long> middleNodeIds) {
        //prepare list of nodes for each street
        var listOfSplitStreetsNodes = new ArrayList<List<Long>>();

        //initialize list of nodes
        for (int i = 0; i < middleNodeIds.size() + 1; i++)
            listOfSplitStreetsNodes.add(new ArrayList<>());

        //split nodes of base street
        int streetPartId = 0;
        for (Long nodeId : street.getNodesIds()) {
            if (middleNodeIds.contains(nodeId)) {
                listOfSplitStreetsNodes.get(streetPartId).add(nodeId);
                listOfSplitStreetsNodes.get(streetPartId + 1).add(nodeId);
                streetPartId++;
            } else
                listOfSplitStreetsNodes.get(streetPartId).add(nodeId);
        }

        //prepare Street objects

        //return list of Streets
        return listOfSplitStreetsNodes
                .stream()
                .map(nodeIds -> new Street(street.getName(), nodeIds))
                .collect(Collectors.toList());
    }

}
