package com.agh.bsct.api.entities.citydata;

import java.util.List;

public class CityDataDTO {
    private List<NodeDTO> nodes;
    private List<StreetDTO> streets;

    public CityDataDTO(List<NodeDTO> nodes, List<StreetDTO> streets) {
        this.nodes = nodes;
        this.streets = streets;
    }

    public List<StreetDTO> getStreets() {
        return streets;
    }

    public List<NodeDTO> getNodes() {
        return nodes;
    }
}
