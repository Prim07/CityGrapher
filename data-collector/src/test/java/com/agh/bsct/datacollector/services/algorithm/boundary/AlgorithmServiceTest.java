package com.agh.bsct.datacollector.services.algorithm.boundary;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class AlgorithmServiceTest {

    private AlgorithmService algorithmService;
    private ObjectNode jsonGraph = new ObjectNode(JsonNodeFactory.instance);

    @BeforeEach
    void setUp() {
        algorithmService = new AlgorithmService();
    }

    @Test
    void shouldCallAlgorithmEndpoint() {
        algorithmService.run("Bochnia", jsonGraph);
    }

    @Test
    void shouldResultBeNotNull() {
        ObjectNode result = algorithmService.run("Bochnia", jsonGraph);
        Assert.assertNotNull(result);
    }
}