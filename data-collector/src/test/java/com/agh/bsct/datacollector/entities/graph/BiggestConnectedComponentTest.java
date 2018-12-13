package com.agh.bsct.datacollector.entities.graph;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BiggestConnectedComponentTest {

    private static final int LOOP_COUNT = 1000;

    @Test
    void shouldBeTheSameWhenMilocinIsCalculatedMultipleTimes() {
        final String filename = "src/test/resources/milocin.txt";

        var graph = initGraph(filename);

        List<GraphNode> biggestCommonComponentToCompare = graph.findBiggestConnectedComponent();

        for (int i = 0; i < LOOP_COUNT; i++) {
            List<GraphNode> biggestCommonComponent = graph.findBiggestConnectedComponent();
            assertEquals(biggestCommonComponentToCompare.size(), biggestCommonComponent.size());
            assertEquals(biggestCommonComponentToCompare, biggestCommonComponent);
        }
    }

    @Test
    void shouldBeTheSameWhenLancutIsCalculatedMultipleTimes() {
        final String filename = "src/test/resources/lancut.txt";

        var graph = initGraph(filename);

        List<GraphNode> biggestCommonComponentToCompare = graph.findBiggestConnectedComponent();

        for (int i = 0; i < LOOP_COUNT; i++) {
            List<GraphNode> biggestCommonComponent = graph.findBiggestConnectedComponent();
            assertEquals(biggestCommonComponentToCompare.size(), biggestCommonComponent.size());
            assertEquals(biggestCommonComponentToCompare, biggestCommonComponent);
        }
    }

    @Test
    void shouldBeTheSameWhenTarnowIsCalculatedMultipleTimes() {
        final String filename = "src/test/resources/tarnow.txt";

        var graph = initGraph(filename);

        List<GraphNode> biggestCommonComponentToCompare = graph.findBiggestConnectedComponent();

        for (int i = 0; i < LOOP_COUNT; i++) {
            List<GraphNode> biggestCommonComponent = graph.findBiggestConnectedComponent();
            assertEquals(biggestCommonComponentToCompare.size(), biggestCommonComponent.size());
            assertEquals(biggestCommonComponentToCompare, biggestCommonComponent);
        }
    }

    private Graph initGraph(String filename) {
        Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap = new HashMap<>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            String linesForOneMapElement = null;
            while (line != null) {

                if (line.matches("\\s+")) {
                    String[] stringIds = linesForOneMapElement.split("\\s+");

                    GraphNode graphNode = null;
                    List<GraphEdge> graphEdges = new ArrayList<>();

                    for (String stringId : stringIds) {
                        Long id = Long.valueOf(stringId);

                        if (graphNode == null) {
                            graphNode = new GraphNode(id, 1);
                        } else {
                            graphEdges.add(new GraphEdge(new GraphNode(id, 1), 1));
                        }
                    }

                    nodeToEdgesIncidenceMap.put(graphNode, graphEdges);

                } else {
                    linesForOneMapElement = (linesForOneMapElement == null) ? line : (linesForOneMapElement + line);
                }

                line = reader.readLine();
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        var graph = new Graph();
        graph.setNodeToEdgesIncidenceMap(nodeToEdgesIncidenceMap);
        return graph;
    }
}
