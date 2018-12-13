package com.agh.bsct.datacollector.services.algorithm.boundary;

import com.agh.bsct.api.entities.algorithmresult.AlgorithmResultDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static com.agh.bsct.datacollector.controllers.config.PathsConstants.ALGORITHM_ROOT_PATH;

@Service
public class AlgorithmService {

    private static final String ALGORITHM_PATH = ALGORITHM_ROOT_PATH + "/algorithm/";

    private final RestTemplate restTemplate;

    public AlgorithmService() {
        this.restTemplate = new RestTemplateBuilder().build();
    }

    public ObjectNode run(GraphDataDTO graphDataDTO) {
        var uri = new DefaultUriBuilderFactory(ALGORITHM_PATH).builder()
                .build()
                .toString();
        return restTemplate.postForObject(uri, graphDataDTO, ObjectNode.class);
    }

    public AlgorithmResultDTO getResult(String taskId) {
        var uri = new DefaultUriBuilderFactory(ALGORITHM_PATH).builder()
                .path(taskId)
                .build()
                .toString();
        return restTemplate.getForObject(uri, AlgorithmResultDTO.class);
    }
}
