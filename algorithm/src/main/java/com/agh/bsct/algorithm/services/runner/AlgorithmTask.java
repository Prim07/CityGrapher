package com.agh.bsct.algorithm.services.runner;

import com.agh.bsct.algorithm.entities.graph.Graph;
import com.agh.bsct.api.entities.algorithmresult.AlgorithmResultDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;

import java.util.List;
import java.util.Optional;

public class AlgorithmTask {

    private final String id;
    private final GraphDataDTO graphDataDTO;
    private Graph graph;
    private AlgorithmCalculationStatus status;
    private List<Integer> hospitalIds;
    private AlgorithmResultDTO algorithmResultDTO;

    AlgorithmTask(String id, GraphDataDTO graphDataDTO, Graph graph) {
        this.id = id;
        this.graphDataDTO = graphDataDTO;
        this.graph = graph;
        this.status = AlgorithmCalculationStatus.NOT_STARTED;
    }

    public String getId() {
        return id;
    }

    public GraphDataDTO getGraphDataDTO() {
        return graphDataDTO;
    }

    public Graph getGraph() {
        return graph;
    }

    public AlgorithmCalculationStatus getStatus() {
        return status;
    }

    public void setStatus(AlgorithmCalculationStatus status) {
        this.status = status;
    }

    public Optional<List<Integer>> getHospitalIds() {
        return Optional.ofNullable(hospitalIds);
    }

    public void setHospitalIds(List<Integer> hospitalIds) {
        this.hospitalIds = hospitalIds;
    }

    public Optional<AlgorithmResultDTO> getAlgorithmResultDTO() {
        return Optional.ofNullable(algorithmResultDTO);
    }

    public void setAlgorithmResultDTO(AlgorithmResultDTO algorithmResultDTO) {
        this.algorithmResultDTO = algorithmResultDTO;
    }
}
