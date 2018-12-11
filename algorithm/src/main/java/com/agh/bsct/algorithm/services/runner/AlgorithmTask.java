package com.agh.bsct.algorithm.services.runner;

import com.agh.bsct.algorithm.entities.graph.Graph;
import com.agh.bsct.api.entities.algorithmresult.AlgorithmResultDTO;

import java.util.Optional;

public class AlgorithmTask {

    private final String id;
    private AlgorithmCalculationStatus status;
    private Graph graphData;
    private AlgorithmResultDTO algorithmResultDTO;

    AlgorithmTask(String id, Graph graphData) {
        this.id = id;
        this.graphData = graphData;
        this.status = AlgorithmCalculationStatus.NOT_STARTED;
    }

    public String getId() {
        return id;
    }

    public Graph getGraphData() {
        return graphData;
    }

    public AlgorithmCalculationStatus getStatus() {
        return status;
    }

    public void setStatus(AlgorithmCalculationStatus status) {
        this.status = status;
    }

    public Optional<AlgorithmResultDTO> getAlgorithmResultDTO() {
        return Optional.ofNullable(algorithmResultDTO);
    }

    public void setAlgorithmResultDTO(AlgorithmResultDTO algorithmResultDTO) {
        this.algorithmResultDTO = algorithmResultDTO;
    }
}
