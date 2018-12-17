package com.agh.bsct.api.entities.algorithmorder;

import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class AlgorithmOrderDTO {

    @NotNull
    private Integer numberOfResults;

    @NotNull
    private GraphDataDTO graphDataDTO;

    public AlgorithmOrderDTO(Integer numberOfResults, GraphDataDTO graphDataDTO) {
        this.numberOfResults = numberOfResults;
        this.graphDataDTO = graphDataDTO;
    }
}
