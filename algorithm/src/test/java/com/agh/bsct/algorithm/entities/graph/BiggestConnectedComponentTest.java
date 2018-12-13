package com.agh.bsct.datacollector.entities.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BiggestConnectedComponentTest {

    private static final String SRC_TEST_RESOURCES_PATH = "src/test/resources/";

    private final GraphInitializer graphInitializer = new GraphInitializer();

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

        var biggestCommonComponentToCompare = graph.findBiggestConnectedComponent();

        int loopCount = 1000;
        for (int i = 0; i < loopCount; i++) {
            var biggestCommonComponent = graph.findBiggestConnectedComponent();

            assertEquals(biggestCommonComponentToCompare.size(), biggestCommonComponent.size());
            assertEquals(biggestCommonComponentToCompare, biggestCommonComponent);
        }
    }
}
