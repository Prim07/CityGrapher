package com.agh.bsct.datacollector.entities.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BiggestConnectedComponentTest {

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

        var biggestCommonComponentToCompare = graph.getBiggestConnectedComponent();

        for (int i = 0; i < LOOP_COUNT; i++) {
            var biggestCommonComponent = graph.getBiggestConnectedComponent();

            assertEquals(biggestCommonComponentToCompare.size(), biggestCommonComponent.size());
            assertEquals(biggestCommonComponentToCompare, biggestCommonComponent);
        }
    }
}
