package com.agh.bsct.algorithm.algorithms;

class SAAlgorithmTest {

   /* private static int NUMBER_OF_RESULTS = 1;

    private AsyncAlgorithmTaskRunner asyncAlgorithmTaskRunner;
    private AlgorithmTask algorithmTask;

    @BeforeEach
    void setUp() {
        var graphInitializer = new GraphInitializer();
        var graph = graphInitializer.initGraph(SRC_TEST_RESOURCES_PATH + "tarnow.txt");

        var graphDataService = new GraphDataService();
        var graphService = new GraphService(graphDataService);
        var saAlgorithm = new SAAlgorithm(new AlgorithmFunctionsService(), new CrossingsService(),
                new AlgorithmTaskMapper(), graphService, mock(GnuplotOutputWriter.class));
        this.asyncAlgorithmTaskRunner = new AsyncAlgorithmTaskRunner(BeanContext);

        var taskId = UUID.randomUUID().toString();
        var algorithm = "sa";
        var algorithmOrderDTO = new AlgorithmOrderDTO(NUMBER_OF_RESULTS, mock(GraphDataDTO.class), algorithm);
        this.algorithmTask = new AlgorithmTask(taskId, algorithmOrderDTO, graph);
    }

    @Test
    void shouldRunAlgorithm() {
        asyncAlgorithmTaskRunner.run(algorithmTask);
    }

    @Test
    void shouldWork() {
        asyncAlgorithmTaskRunner.run(algorithmTask);
    }*/

}