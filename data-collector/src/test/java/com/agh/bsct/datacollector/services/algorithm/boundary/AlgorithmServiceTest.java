package com.agh.bsct.datacollector.services.algorithm.boundary;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class AlgorithmServiceTest {
    private AlgorithmService algorithmService;

    @BeforeEach
    void setUp() {
        algorithmService = new AlgorithmService();
    }

    @Test
    void shouldCallAlgorithmEndpoint() {
        algorithmService.run("Bochnia");
    }

    @Test
    void shouldResultBeNotNull() {
        String result = algorithmService.run("Bochnia");
        Assert.assertNotNull(result);
    }
}