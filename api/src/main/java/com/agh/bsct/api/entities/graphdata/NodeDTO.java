package com.agh.bsct.api.entities.graphdata;

public class NodeDTO {

    private com.agh.bsct.api.entities.citydata.NodeDTO nodeDTO;
    private Integer weight;

    public NodeDTO(com.agh.bsct.api.entities.citydata.NodeDTO nodeDTO, Integer weight) {
        this.nodeDTO = nodeDTO;
        this.weight = weight;
    }

    public com.agh.bsct.api.entities.citydata.NodeDTO getNodeDTO() {
        return nodeDTO;
    }

    public Integer getWeight() {
        return weight;
    }

}
