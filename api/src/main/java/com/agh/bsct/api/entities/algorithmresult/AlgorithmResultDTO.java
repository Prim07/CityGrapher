package com.agh.bsct.api.entities.algorithmresult;

import com.agh.bsct.api.entities.graphdata.GraphDataDTO;

import java.util.List;

public class AlgorithmResultDTO {

    private GraphDataDTO graphDataDTO;
    private List<Integer> hospitalIds;

    public AlgorithmResultDTO(GraphDataDTO graphDataDTO, List<Integer> hospitalIds) {
        this.graphDataDTO = graphDataDTO;
        this.hospitalIds = hospitalIds;
    }

    public GraphDataDTO getGraphDataDTO() {
        return graphDataDTO;
    }

    public void setGraphDataDTO(GraphDataDTO graphDataDTO) {
        this.graphDataDTO = graphDataDTO;
    }

    public List<Integer> getHospitalIds() {
        return hospitalIds;
    }

    public void setHospitalIds(List<Integer> hospitalIds) {
        this.hospitalIds = hospitalIds;
    }
}
