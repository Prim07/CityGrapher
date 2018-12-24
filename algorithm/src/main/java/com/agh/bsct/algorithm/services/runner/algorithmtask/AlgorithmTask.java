package com.agh.bsct.algorithm.services.runner.algorithmtask;

import com.agh.bsct.algorithm.entities.graph.Graph;
import com.agh.bsct.api.entities.algorithmorder.AlgorithmOrderDTO;
import com.agh.bsct.api.entities.algorithmresult.AlgorithmResultDTO;
import com.agh.bsct.api.entities.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;

import java.util.List;
import java.util.Optional;

public class AlgorithmTask {

    private final String taskId;
    private final GraphDataDTO graphDataDTO;
    private final Integer numberOfResults;
    private final String algorithm;
    private Graph graph;
    private AlgorithmCalculationStatus status;
    private List<GeographicalNodeDTO> hospitals;
    private AlgorithmResultDTO algorithmResultDTO;

    public AlgorithmTask(String taskId, AlgorithmOrderDTO algorithmOrderDTO, Graph graph) {
        this.taskId = taskId;
        this.graphDataDTO = algorithmOrderDTO.getGraphDataDTO();
        this.numberOfResults = algorithmOrderDTO.getNumberOfResults();
        this.algorithm = algorithmOrderDTO.getAlgorithm();
        this.graph = graph;
        this.status = AlgorithmCalculationStatus.NOT_STARTED;
    }

    public String getTaskId() {
        return taskId;
    }

    public GraphDataDTO getGraphDataDTO() {
        return graphDataDTO;
    }

    public Integer getNumberOfResults() {
        return numberOfResults;
    }

    public String getAlgorithm() {
        return algorithm;
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

    public Optional<List<GeographicalNodeDTO>> getHospitals() {
        return Optional.ofNullable(hospitals);
    }

    public void setHospitals(List<GeographicalNodeDTO> hospitals) {
        this.hospitals = hospitals;
    }

    public Optional<AlgorithmResultDTO> getAlgorithmResultDTO() {
        return Optional.ofNullable(algorithmResultDTO);
    }

    public void setAlgorithmResultDTO(AlgorithmResultDTO algorithmResultDTO) {
        this.algorithmResultDTO = algorithmResultDTO;
    }
}
