package com.agh.bsct.algorithm.entities.graph;

import com.agh.bsct.algorithm.services.entities.graph.GraphService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.agh.bsct.algorithm.entities.graph.GraphInitializer.SRC_TEST_RESOURCES_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BiggestConnectedComponentTest {

    private final GraphInitializer graphInitializer = new GraphInitializer();
    private final GraphService graphService = new GraphService();

    @Test
    void shouldBeTheSameWhenMilocinIsCalculatedMultipleTimes() {
        final String filename = SRC_TEST_RESOURCES_PATH + "milocin.txt";

        shouldBeTheSameWhenCalculatedMultipleTimes(filename);
    }

    @Test
    void shouldBeTheSameWhenLancutIsCalculatedMultipleTimes() {
        final String filename = SRC_TEST_RESOURCES_PATH + "lancut.txt";

        shouldBeTheSameWhenCalculatedMultipleTimes(filename);
    }

    @Test
    void shouldBeTheSameWhenTarnowIsCalculatedMultipleTimes() {
        final String filename = SRC_TEST_RESOURCES_PATH + "tarnow.txt";

        shouldBeTheSameWhenCalculatedMultipleTimes(filename);
    }

    private void shouldBeTheSameWhenCalculatedMultipleTimes(String filename) {
        var graph = graphInitializer.initGraph(filename);

        Map<GraphNode, List<GraphEdge>> incidenceMap = graph.getIncidenceMap();
        var biggestCommonComponentToCompare = graphService.findBiggestConnectedComponent(incidenceMap);

        int loopCount = 1000;
        for (int i = 0; i < loopCount; i++) {
            var biggestCommonComponent = graphService.findBiggestConnectedComponent(incidenceMap);

            assertEquals(biggestCommonComponentToCompare.size(), biggestCommonComponent.size());
            assertEquals(biggestCommonComponentToCompare, biggestCommonComponent);
        }
    }
}
