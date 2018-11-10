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
        List<Node> nodes = mapToNodes(overpassQueryResult);
        updateWithCrossingInformation(nodes, crossingsIds);
        return new CityData(nodes, streetsAfterSplitting);
    }

    private Map<Long, Integer> getNodeIdToOccurrencesInStreetCountMap(Set<Street> streets) {
        Map<Long, Integer> nodeIdsToOccurrencesInStreets = new HashMap<>();
        streets.stream()
                .map(Street::getNodesIds)
                .flatMap(Collection::stream)
                .forEach(nodeId -> nodeIdsToOccurrencesInStreets.merge(nodeId, 1, (a, b) -> a + b));
        return nodeIdsToOccurrencesInStreets;
    }

    private List<Long> getCrossingIds(Map<Long, Integer> nodeIdToOccurrencesInStreetCount) {
        return nodeIdToOccurrencesInStreetCount.entrySet().stream()
                .filter(this::isNodesOccurrenceGreaterThanOne)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private boolean isNodesOccurrenceGreaterThanOne(Map.Entry<Long, Integer> entry) {
        return entry.getValue() > 1;
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

    private List<Street> splitStreet(Street street, List<Long> middleNodeIds) {
        ArrayList<List<Long>> listOfSplitStreetsNodes = getInitializedWithEmptyLists(middleNodeIds.size() + 1);
        splitNodesOfBaseStreet(street, middleNodeIds, listOfSplitStreetsNodes);
        return mapToStreets(street, listOfSplitStreetsNodes);
    }

    private ArrayList<List<Long>> getInitializedWithEmptyLists(int size) {
        var splitStreetsNodes = new ArrayList<List<Long>>(size);
        for (int i = 0; i < size; i++) {
            splitStreetsNodes.add(i, new ArrayList<>());
        }
        return splitStreetsNodes;
    }

    private void splitNodesOfBaseStreet(Street street, List<Long> middleNodeIds,
                                        ArrayList<List<Long>> listOfSplitStreetsNodes) {
        var streetPartId = 0;
        for (Long nodeId : street.getNodesIds()) {
            if (middleNodeIds.contains(nodeId)) {
                listOfSplitStreetsNodes.get(streetPartId).add(nodeId);
                listOfSplitStreetsNodes.get(streetPartId + 1).add(nodeId);
                streetPartId++;
            } else {
                listOfSplitStreetsNodes.get(streetPartId).add(nodeId);
            }
        }
    }

    private List<Street> mapToStreets(Street street, ArrayList<List<Long>> listOfSplitStreetsNodes) {
        return listOfSplitStreetsNodes
                .stream()
                .map(nodeIds -> new Street(street.getName(), nodeIds))
                .collect(Collectors.toList());
    }

    private List<Node> mapToNodes(OverpassQueryResult overpassQueryResult) {
        return overpassQueryResult.getElements().stream()
                    .filter(element -> NODE_TYPE.equals(element.getType()))
                    .map(element -> new Node(element.getId(), element.getLon(), element.getLat()))
                    .collect(Collectors.toList());
    }

    private void updateWithCrossingInformation(List<Node> nodes, List<Long> crossingsIds) {
        nodes.forEach(node -> {
            if (crossingsIds.contains(node.getId())) {
                node.setCrossing(true);
            }
        });
    }

}
