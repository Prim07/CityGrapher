package com.agh.bsct.datacollector.services.filter;

import com.agh.bsct.datacollector.library.adapter.queryresult.Element;
import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResultFilterService {

    private static final String NEXT_PART_OF_STREET_POSTFIX = "_part";

    public OverpassQueryResult removeAreaTags(OverpassQueryResult queryResult) {
        var iterator = queryResult.getElements().iterator();
        while (iterator.hasNext()) {
            var element = iterator.next();
            var area = element.getTags().getArea();
            var type = element.getType();
            if ("yes".equals(area) || type.equals("area")) {
                iterator.remove();
            }
        }

        return queryResult;
    }

    public HashMap<String, LinkedHashSet<Long>> joinRoads(OverpassQueryResult queryResult) {
        HashMap<String, List<List<Long>>> streetNameToListOfNodes = getStreetNameToListOfNodes(queryResult);
        HashMap<String, LinkedList<Long>> streetNameToNodes =
                getMapStreetNameToNodes(streetNameToListOfNodes, new HashMap<>());
        return getMapWithoutDuplicateNodes(streetNameToNodes);
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

    private HashMap<String, LinkedList<Long>> getMapStreetNameToNodes(
            HashMap<String, List<List<Long>>> streetNameToListOfNodes,
            HashMap<String, LinkedList<Long>> streetNameToNodes) {
        for (Map.Entry<String, List<List<Long>>> currentStreetNameToNodesPart : streetNameToListOfNodes.entrySet()) {
            List<List<Long>> currentEntryListOfNodes = currentStreetNameToNodesPart.getValue();

            LinkedList<Long> orderedJoinedNodes = initializeOrderedJoinedNodesWithFirstPart(currentEntryListOfNodes);

            var maxNumberOfIterations = currentEntryListOfNodes.size();
            var iterationsCounter = 0;

            while (isNextIterationAvailable(currentEntryListOfNodes, maxNumberOfIterations, iterationsCounter)) {
                iterationsCounter++;
                orderedJoinedNodes = joinNewNodesPartToOrderedJoinedNodes(currentEntryListOfNodes, orderedJoinedNodes);
            }

            String streetName = currentStreetNameToNodesPart.getKey();

            if (isStreetBuiltOfManyParts(currentEntryListOfNodes)) {
                streetNameToNodes = getMapForRemainingStreets(currentEntryListOfNodes, streetNameToNodes, streetName);
            }

            streetNameToNodes.put(streetName, orderedJoinedNodes);
        }

        return streetNameToNodes;
    }

    private LinkedList<Long> initializeOrderedJoinedNodesWithFirstPart(List<List<Long>> currentEntryListOfNodes) {
        var orderedJoinedNodes = new LinkedList<Long>();
        orderedJoinedNodes.addAll(currentEntryListOfNodes.get(0));
        currentEntryListOfNodes.remove(0);
        return orderedJoinedNodes;
    }

    private boolean isNextIterationAvailable(List<List<Long>> listOfNodes, int maxNumberOfIterations,
                                             int iterationsCounter) {
        return isStreetBuiltOfManyParts(listOfNodes) && iterationsCounter < maxNumberOfIterations;
    }

    private boolean isStreetBuiltOfManyParts(List<List<Long>> currentEntryListOfNodes) {
        return !currentEntryListOfNodes.isEmpty();
    }

    private LinkedList<Long> joinNewNodesPartToOrderedJoinedNodes(List<List<Long>> currentEntryListOfNodes,
                                                                  LinkedList<Long> orderedNodes) {
        Long firstJoinedNodeId = orderedNodes.get(0);
        Long lastJoinedNodeId = orderedNodes.get(orderedNodes.size() - 1);
        var currentEntryListOfNodesIterator = currentEntryListOfNodes.iterator();

        while (currentEntryListOfNodesIterator.hasNext()) {
            List<Long> candidateNodes = currentEntryListOfNodesIterator.next();
            Long firstCandidateNodeToJoinId = candidateNodes.get(0);
            Long lastCandidateNodeToJoinId = candidateNodes.get(candidateNodes.size() - 1);

            if (firstCandidateNodeToJoinId.equals(firstJoinedNodeId)) {
                orderedNodes = joinNodesConnectedInFirstPositions(orderedNodes, candidateNodes);
            } else if (firstCandidateNodeToJoinId.equals(lastJoinedNodeId)) {
                orderedNodes = joinNodesConnectedByFirstCandidateAndLastOrdered(orderedNodes, candidateNodes);
            } else if (lastCandidateNodeToJoinId.equals(firstJoinedNodeId)) {
                orderedNodes = joinNodesConnectedByLastCandidateAndFirstOrdered(orderedNodes, candidateNodes);
            } else if (lastCandidateNodeToJoinId.equals(lastJoinedNodeId)) {
                orderedNodes = joinNodesConnectedInLastPositions(orderedNodes, candidateNodes);
            } else {
                continue;
            }

            currentEntryListOfNodesIterator.remove();
        }

        return orderedNodes;
    }

    private LinkedList<Long> joinNodesConnectedInFirstPositions(LinkedList<Long> orderedJoinedNodes,
                                                                List<Long> candidateNodesToJoin) {
        Collections.reverse(candidateNodesToJoin);
        var orderedJoinedNodesCopy = new LinkedList<>(orderedJoinedNodes);
        orderedJoinedNodes = new LinkedList<>(candidateNodesToJoin);
        orderedJoinedNodes.addAll(orderedJoinedNodesCopy);
        return orderedJoinedNodes;
        //TODO AK niby kod to samo robi, ale zwraca lekko różne wyniki. Sprawdziłbym też inne metody join...
        /*Collections.reverse(orderedJoinedNodes);
        orderedJoinedNodes.addAll(candidateNodesToJoin);*/
    }

    private LinkedList<Long> joinNodesConnectedByFirstCandidateAndLastOrdered(LinkedList<Long> orderedJoinedNodes,
                                                                              List<Long> candidateNodesToJoin) {
        orderedJoinedNodes.addAll(candidateNodesToJoin);
        return orderedJoinedNodes;
    }

    private LinkedList<Long> joinNodesConnectedByLastCandidateAndFirstOrdered(LinkedList<Long> orderedJoinedNodes,
                                                                              List<Long> candidateNodesToJoin) {
        var orderedJoinedNodesCopy = new LinkedList<>(orderedJoinedNodes);
        orderedJoinedNodes = new LinkedList<>(candidateNodesToJoin);
        orderedJoinedNodes.addAll(orderedJoinedNodesCopy);
        return orderedJoinedNodes;
    }

    private LinkedList<Long> joinNodesConnectedInLastPositions(LinkedList<Long> orderedJoinedNodes,
                                                               List<Long> candidateNodesToJoin) {
        Collections.reverse(candidateNodesToJoin);
        orderedJoinedNodes.addAll(candidateNodesToJoin);
        return orderedJoinedNodes;
    }

    private HashMap<String, LinkedList<Long>> getMapForRemainingStreets(
            List<List<Long>> currentEntryListOfNodes, HashMap<String, LinkedList<Long>> streetNameToNodes,
            String streetName) {
        HashMap<String, List<List<Long>>> remainingStreetNameToListOfNodes = new HashMap<>();
        remainingStreetNameToListOfNodes.put(streetName + NEXT_PART_OF_STREET_POSTFIX, currentEntryListOfNodes);
        streetNameToNodes = getMapStreetNameToNodes(remainingStreetNameToListOfNodes, streetNameToNodes);
        return streetNameToNodes;
    }

    private HashMap<String, LinkedHashSet<Long>> getMapWithoutDuplicateNodes(
            HashMap<String, LinkedList<Long>> streetNameToListOfNodes) {
        var streetNameToSetOfNodes = new HashMap<String, LinkedHashSet<Long>>();
        streetNameToListOfNodes.forEach((streetName, listOfNodes) ->
                streetNameToSetOfNodes.put(streetName, new LinkedHashSet<Long>(listOfNodes)));
        return streetNameToSetOfNodes;
    }
}
