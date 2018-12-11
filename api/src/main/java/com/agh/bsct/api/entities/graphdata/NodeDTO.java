package com.agh.bsct.api.entities.graphdata;

import com.agh.bsct.api.entities.citydata.GeographicalNodeDTO;

public class NodeDTO {

    private GeographicalNodeDTO geographicalNodeDTO;
    private Integer weight;

    public NodeDTO(GeographicalNodeDTO geographicalNodeDTO, Integer weight) {
        this.geographicalNodeDTO = geographicalNodeDTO;
        this.weight = weight;
    }

    public GeographicalNodeDTO getGeographicalNodeDTO() {
        return geographicalNodeDTO;
    }

    public Integer getWeight() {
        return weight;
    }

}
