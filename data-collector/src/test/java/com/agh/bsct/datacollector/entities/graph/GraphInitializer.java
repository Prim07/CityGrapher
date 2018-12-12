package com.agh.bsct.datacollector.entities.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GraphInitializer {

    private Map<String, Graph> filenameToGraph = new HashMap<>();

    Graph initGraph(String filename) {
        Graph graph = filenameToGraph.get(filename);

        if (graph == null) {
            graph = new Graph();

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

            filenameToGraph.put(filename, graph);
        }

        return graph;
    }

}