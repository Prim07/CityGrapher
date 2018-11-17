package com.agh.bsct.datacollector.services.result.joiner;

import com.agh.bsct.datacollector.entities.citydata.Street;
import com.agh.bsct.datacollector.library.adapter.queryresult.Element;
import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StreetsJoinerService {

    private static final String WAY_TYPE = "way";
    private static final String NEXT_PART_OF_STREET_POSTFIX = "_part";
    private static final String UNNAMED_STREET_NAME = "unnamed";

    public Set<Street> joinStreets(OverpassQueryResult queryResult) {
        Map<String, Set<Street>> streetNameToStreets = getStreetNameToStreets(queryResult);
        Set<Street> streets = getStreets(streetNameToStreets, new HashSet<>());
        return getStreetsWithoutDuplicateNodes(streets);
    }

    private HashMap<String, Set<Street>> getStreetNameToStreets(OverpassQueryResult queryResult) {
        var iterator = queryResult.getElements().iterator();
        var streetNameToStreets = new HashMap<String, Set<Street>>();

        while (iterator.hasNext()) {
            Element currentElement = iterator.next();
            if (isCorrectWay(currentElement)) {
                String name = setStreetName(currentElement);
                if (!streetNameToStreets.containsKey(name)) {
                    streetNameToStreets.put(name, new HashSet<>());
                }
                var nodes = currentElement.getNodesAsArrayList();
                if (nodes != null) {
                    Set<Street> streets = streetNameToStreets.get(name);
                    streets.add(new Street(name, new ArrayList<>(nodes)));
                }
            }
        }

        return streetNameToStreets;
    }

    private String setStreetName(Element currentElement) {
        var name = currentElement.getTags().getName();
        if (name == null) {
            name = UNNAMED_STREET_NAME;
        }
        return name;
    }

    private boolean isCorrectWay(Element currentElement) {
        return WAY_TYPE.equals(currentElement.getType());
    }

    private Set<Street> getStreets(Map<String, Set<Street>> streetNameToStreets, Set<Street> streets) {
        for (Map.Entry<String, Set<Street>> currentStreetNameToStreetsPart : streetNameToStreets.entrySet()) {
            Set<Street> currentEntryStreets = currentStreetNameToStreetsPart.getValue();

            List<Long> orderedJoinedNodes = initializeOrderedJoinedNodesWithFirstPart(currentEntryStreets);

            var maxNumberOfIterations = currentEntryStreets.size();
            var iterationsCounter = 0;

            while (isNextIterationAvailable(currentEntryStreets, maxNumberOfIterations, iterationsCounter)) {
                iterationsCounter++;
                orderedJoinedNodes = joinNewNodesPartToOrderedJoinedNodes(currentEntryStreets, orderedJoinedNodes);
            }

            String streetName = currentStreetNameToStreetsPart.getKey();

            if (isStreetBuiltOfManyParts(currentEntryStreets)) {
                streets = getMapForRemainingStreets(currentEntryStreets, streets, streetName);
            }

            streets.add(new Street(streetName, new ArrayList<>(orderedJoinedNodes)));
        }

        return streets;
    }

    private List<Long> initializeOrderedJoinedNodesWithFirstPart(Set<Street> currentEntryStreets) {
        Iterator<Street> currentEntryStreetsIterator = currentEntryStreets.iterator();
        var orderedJoinedNodes = new ArrayList<>(currentEntryStreetsIterator.next().getNodesIds());
        currentEntryStreetsIterator.remove();
        return orderedJoinedNodes;
    }

    private boolean isNextIterationAvailable(Set<Street> streets, int maxNumberOfIterations, int iterationsCounter) {
        return isStreetBuiltOfManyParts(streets) && iterationsCounter < maxNumberOfIterations;
    }

    private boolean isStreetBuiltOfManyParts(Set<Street> streets) {
        return !streets.isEmpty();
    }

    private List<Long> joinNewNodesPartToOrderedJoinedNodes(Set<Street> currentEntryStreets,
                                                            List<Long> orderedNodes) {
        Long firstJoinedNodeId = orderedNodes.get(0);
        Long lastJoinedNodeId = orderedNodes.get(orderedNodes.size() - 1);
        var currentEntryListOfNodesIterator = currentEntryStreets.iterator();

        while (currentEntryListOfNodesIterator.hasNext()) {
            Street street = currentEntryListOfNodesIterator.next();
            List<Long> streetNodesIds = street.getNodesIds();
            Long firstCandidateNodeToJoinId = streetNodesIds.get(0);
            Long lastCandidateNodeToJoinId = streetNodesIds.get(streetNodesIds.size() - 1);

            if (firstCandidateNodeToJoinId.equals(firstJoinedNodeId)) {
                orderedNodes = joinNodesConnectedInFirstPositions(orderedNodes, streetNodesIds);
            } else if (firstCandidateNodeToJoinId.equals(lastJoinedNodeId)) {
                orderedNodes = joinNodesConnectedByFirstCandidateAndLastOrdered(orderedNodes, streetNodesIds);
            } else if (lastCandidateNodeToJoinId.equals(firstJoinedNodeId)) {
                orderedNodes = joinNodesConnectedByLastCandidateAndFirstOrdered(orderedNodes, streetNodesIds);
            } else if (lastCandidateNodeToJoinId.equals(lastJoinedNodeId)) {
                orderedNodes = joinNodesConnectedInLastPositions(orderedNodes, streetNodesIds);
            } else {
                continue;
            }

            currentEntryListOfNodesIterator.remove();
        }

        return orderedNodes;
    }

    private List<Long> joinNodesConnectedInFirstPositions(List<Long> orderedJoinedNodes,
                                                          List<Long> candidateNodesToJoin) {
        Collections.reverse(candidateNodesToJoin);
        var orderedJoinedNodesCopy = new ArrayList<>(orderedJoinedNodes);
        orderedJoinedNodes = new ArrayList<>(candidateNodesToJoin);
        orderedJoinedNodes.addAll(orderedJoinedNodesCopy);
        return orderedJoinedNodes;
        //TODO AK niby kod to samo robi, ale zwraca lekko różne wyniki. Sprawdziłbym też inne metody join...
        /*Collections.reverse(orderedJoinedNodes);
        orderedJoinedNodes.addAll(candidateNodesToJoin);*/
    }

    private List<Long> joinNodesConnectedByFirstCandidateAndLastOrdered(List<Long> orderedJoinedNodes,
                                                                        List<Long> candidateNodesToJoin) {
        orderedJoinedNodes.addAll(candidateNodesToJoin);
        return orderedJoinedNodes;
    }

    private List<Long> joinNodesConnectedByLastCandidateAndFirstOrdered(List<Long> orderedJoinedNodes,
                                                                        List<Long> candidateNodesToJoin) {
        var orderedJoinedNodesCopy = new ArrayList<>(orderedJoinedNodes);
        orderedJoinedNodes = new ArrayList<>(candidateNodesToJoin);
        orderedJoinedNodes.addAll(orderedJoinedNodesCopy);
        return orderedJoinedNodes;
    }

    private List<Long> joinNodesConnectedInLastPositions(List<Long> orderedJoinedNodes,
                                                         List<Long> candidateNodesToJoin) {
        Collections.reverse(candidateNodesToJoin);
        orderedJoinedNodes.addAll(candidateNodesToJoin);
        return orderedJoinedNodes;
    }

    private Set<Street> getMapForRemainingStreets(Set<Street> currentEntryStreets, Set<Street> streets,
                                                  String streetName) {
        HashMap<String, Set<Street>> remainingStreetNameToStreets = new HashMap<>();
        remainingStreetNameToStreets.put(streetName + NEXT_PART_OF_STREET_POSTFIX, currentEntryStreets);
        streets = getStreets(remainingStreetNameToStreets, streets);
        return streets;
    }

    private Set<Street> getStreetsWithoutDuplicateNodes(Set<Street> streets) {
        return streets.stream()
                .map(street -> new Street(street.getName(), mapToNodesWithoutDuplicates(street.getNodesIds())))
                .collect(Collectors.toSet());
    }

    private List<Long> mapToNodesWithoutDuplicates(List<Long> nodesIds) {
        return new ArrayList<>(new LinkedHashSet<>(nodesIds));
    }
}
