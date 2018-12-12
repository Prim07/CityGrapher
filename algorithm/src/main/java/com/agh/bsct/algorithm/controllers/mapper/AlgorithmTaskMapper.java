package com.agh.bsct.algorithm.controllers.mapper;

import com.agh.bsct.algorithm.services.runner.AlgorithmTask;
import com.agh.bsct.api.entities.algorithmresult.AlgorithmResultDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlgorithmTaskMapper {

    public AlgorithmResultDTO mapToAlgorithmResultDTO(AlgorithmTask algorithmTask, List<Integer> hospitalIds) {
        return AlgorithmResultDTO.builder()
                .id(algorithmTask.getId())
                .status(algorithmTask.getStatus().toString())
                .graphDataDTO(algorithmTask.getGraphDataDTO())
                .hospitalIds(hospitalIds)
                .build();
    }

}
