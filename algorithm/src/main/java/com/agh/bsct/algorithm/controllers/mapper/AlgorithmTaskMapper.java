package com.agh.bsct.algorithm.controllers.mapper;

import com.agh.bsct.algorithm.services.runner.AlgorithmTask;
import com.agh.bsct.api.entities.algorithmresult.AlgorithmResultDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class AlgorithmTaskMapper {

    public AlgorithmResultDTO mapToAlgorithmResultDTO(AlgorithmTask algorithmTask) {
        return AlgorithmResultDTO.builder()
                .id(algorithmTask.getId())
                .status(algorithmTask.getStatus().toString())
                .graphDataDTO(algorithmTask.getGraphDataDTO())
                .hospitalIds(algorithmTask.getHospitalIds().orElse(Collections.emptyList()))
                .build();
    }

}
