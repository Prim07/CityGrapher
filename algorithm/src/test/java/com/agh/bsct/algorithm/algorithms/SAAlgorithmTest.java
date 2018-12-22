package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.algorithms.writer.GnuplotValuesWriter;
import com.agh.bsct.algorithm.controllers.mapper.AlgorithmTaskMapper;
import com.agh.bsct.algorithm.entities.graph.GraphInitializer;
import com.agh.bsct.algorithm.services.entities.graph.GraphService;
import com.agh.bsct.algorithm.services.entities.graphdata.GraphDataService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.algorithm.services.runner.asyncrunner.AsyncAlgorithmTaskRunner;
import com.agh.bsct.api.entities.algorithmorder.AlgorithmOrderDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.agh.bsct.algorithm.entities.graph.GraphInitializer.SRC_TEST_RESOURCES_PATH;
import static org.mockito.Mockito.mock;

class SAAlgorithmTest {

    private static int NUMBER_OF_RESULTS = 1;

    private AsyncAlgorithmTaskRunner asyncAlgorithmTaskRunner;
    private AlgorithmTask algorithmTask;

    @BeforeEach
    void setUp() {
        var graphInitializer = new GraphInitializer();
        var graph = graphInitializer.initGraph(SRC_TEST_RESOURCES_PATH + "tarnow.txt");

        var graphDataService = new GraphDataService();
        var graphService = new GraphService(graphDataService);
        var saAlgorithm = new SAAlgorithm(new AlgorithmTaskMapper(), graphService,
                mock(GnuplotValuesWriter.class));
        this.asyncAlgorithmTaskRunner = new AsyncAlgorithmTaskRunner(saAlgorithm);

        var taskId = UUID.randomUUID().toString();
        var algorithmOrderDTO = new AlgorithmOrderDTO(NUMBER_OF_RESULTS, mock(GraphDataDTO.class));
        this.algorithmTask = new AlgorithmTask(taskId, algorithmOrderDTO, graph);
    }

    @Test
    void shouldRunAlgorithm() {
        asyncAlgorithmTaskRunner.run(algorithmTask);
    }

    @Test
    void shouldWork() {
        asyncAlgorithmTaskRunner.run(algorithmTask);
    }

}