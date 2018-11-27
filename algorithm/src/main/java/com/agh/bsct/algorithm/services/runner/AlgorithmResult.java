package com.agh.bsct.algorithm.services.runner;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;

public class AlgorithmResult {

    private AlgorithmCalculationStatus status;
    private Optional<ObjectNode> jsonResult = Optional.empty();

    public AlgorithmCalculationStatus getStatus() {
        return status;
    }

    public Optional<ObjectNode> getJsonResult() {
        return jsonResult;
    }

    public void setStatus(AlgorithmCalculationStatus status) {
        this.status = status;
    }

    public void setJsonResult(ObjectNode jsonResult) {
        this.jsonResult = Optional.ofNullable(jsonResult);
    }
}
