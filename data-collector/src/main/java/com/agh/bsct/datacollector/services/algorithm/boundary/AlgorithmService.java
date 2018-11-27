package com.agh.bsct.datacollector.services.algorithm.boundary;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static com.agh.bsct.datacollector.controllers.config.PathsConstants.ROOT_PATH;

@Service
public class AlgorithmService {

    private static final String ALGORITHM_PATH = ROOT_PATH + "/algorithm/";
    private static final String CITY_PARAM_NAME = "city";

    private final RestTemplate restTemplate;

    public AlgorithmService() {
        this.restTemplate = new RestTemplateBuilder().build();
    }

    public String run(String cityName) {
        var uri = new DefaultUriBuilderFactory().builder().path(ALGORITHM_PATH)
                .queryParam(CITY_PARAM_NAME, cityName)
                .build()
                .toString();
        return restTemplate.getForObject(uri, String.class);
    }
}
