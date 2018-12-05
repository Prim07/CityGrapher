package com.agh.bsct.algorithm.services.runner;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;

public class AlgorithmTask {

    private final String id;
    private AlgorithmCalculationStatus status;
    //TODO tutaj musimy używać nie ObjectNode, a jakiejś sparsowanej encji - tak samo, jak w przypadku DataCollector było GraphData
    private ObjectNode graphData;
    private Optional<ObjectNode> jsonResult = Optional.empty();

    public AlgorithmTask(String id, ObjectNode graphData) {
        this.id = id;
        this.graphData = graphData;
        this.status = AlgorithmCalculationStatus.NOT_STARTED;
    }

    public String getId() {
        return id;
    }

    public AlgorithmCalculationStatus getStatus() {
        return status;
    }

    public Optional<ObjectNode> getJsonResult() {
        return jsonResult;
    }

    public ObjectNode getGraphData() {
        return graphData;
    }

    public void setStatus(AlgorithmCalculationStatus status) {
        this.status = status;
    }

    public void setJsonResult(ObjectNode jsonResult) {
        this.jsonResult = Optional.ofNullable(jsonResult);
    }
}
