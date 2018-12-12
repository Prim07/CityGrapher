package com.agh.bsct.api.entities.algorithmresult;

import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
public class AlgorithmResultDTO {

    @NotNull
    private String id;

    @NotNull
    private String status;

    @NotNull
    private GraphDataDTO graphDataDTO;

    @NotNull
    private List<Integer> hospitalIds;

    public AlgorithmResultDTO(String id, GraphDataDTO graphDataDTO, List<Integer> hospitalIds) {
        this.id = id;
        this.graphDataDTO = graphDataDTO;
        this.hospitalIds = hospitalIds;
    }

    public AlgorithmResultDTO(String id, String status, GraphDataDTO graphDataDTO, List<Integer> hospitalIds) {
        this.id = id;
        this.status = status;
        this.graphDataDTO = graphDataDTO;
        this.hospitalIds = hospitalIds;
    }
}
