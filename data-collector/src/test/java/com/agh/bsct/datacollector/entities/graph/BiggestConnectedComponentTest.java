package com.agh.bsct.datacollector.entities.graph;

import org.junit.jupiter.api.BeforeAll;
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

    private static Graph graph = new Graph();
    private static final String filename = "src/test/resources/milocin.txt";

    @BeforeAll
    static void init() {
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

        graph.setNodeToEdgesIncidenceMap(nodeToEdgesIncidenceMap);
    }

    @Test
    void shouldBeTheSameWhenCalculatedMultipleTimes() {
        List<GraphNode> biggestCommonComponentToCompare = graph.getBiggestConnectedComponent();

        for (int i = 0; i < 1000; i++) {
            List<GraphNode> biggestCommonComponent = graph.getBiggestConnectedComponent();
            assertEquals(biggestCommonComponentToCompare.size(), biggestCommonComponent.size());
            assertEquals(biggestCommonComponentToCompare, biggestCommonComponent);
        }
    }
}
