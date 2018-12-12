package com.agh.bsct.datacollector.entities.graph;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FloydWarshallAlgorithmTest {

    private final int LOOP_COUNT = 1000;
    private final GraphInitializer graphInitializer = new GraphInitializer();

    @Test
    void shouldBeTheSameWhenMilocinIsCalculatedMultipleTimes() {
        final String filename = "src/test/resources/milocin.txt";

        shouldBeTheSameWhenCalculatedMultipleTimes(filename);
    }

    @Test
    void shouldBeTheSameWhenLancutIsCalculatedMultipleTimes() {
        final String filename = "src/test/resources/lancut.txt";

        shouldBeTheSameWhenCalculatedMultipleTimes(filename);
    }

    @Test
    void shouldBeTheSameWhenTarnowIsCalculatedMultipleTimes() {
        final String filename = "src/test/resources/tarnow.txt";

        shouldBeTheSameWhenCalculatedMultipleTimes(filename);
    }

    private void shouldBeTheSameWhenCalculatedMultipleTimes(String filename) {
        var graph = graphInitializer.initGraph(filename);

        graph.setShortestPathsDistances(null);
        graph.calculateShortestPathsDistances();
        var shortestPathsDistancesToCompare = graph.getShortestPathsDistances();
        var shortestPathsDistancesListToCompare = convert2DArrayToList(shortestPathsDistancesToCompare);

        for (int i = 0; i < LOOP_COUNT; i++) {
            graph.setShortestPathsDistances(null);
            graph.calculateShortestPathsDistances();
            var shortestPathsDistances = graph.getShortestPathsDistances();
            var shortestPathsDistancesList = convert2DArrayToList(shortestPathsDistances);

            for (ArrayList<Double> distances : shortestPathsDistancesList) {
                for (Double distance : distances) {
                    assertNotEquals(Double.MAX_VALUE, distance);
                }
            }
            assertEquals(shortestPathsDistancesListToCompare.size(), shortestPathsDistancesList.size());
            assertEquals(shortestPathsDistancesListToCompare, shortestPathsDistancesList);
        }
    }

    private ArrayList<ArrayList<Double>> convert2DArrayToList(Double[][] shortestPathsDistances) {
        var shortestPathsDistancesList = new ArrayList<ArrayList<Double>>();

        for (Double[] shortestPathsDistance : shortestPathsDistances) {
            shortestPathsDistancesList.add(new ArrayList<>(Arrays.asList(shortestPathsDistance)));
        }

        return shortestPathsDistancesList;
    }
}