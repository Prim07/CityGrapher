package com.agh.bsct.api.entities.citydata;

import java.util.List;

public class CityDataDTO {
    private List<GeographicalNodeDTO> geographicalNodes;
    private List<StreetDTO> streets;

    public CityDataDTO(List<GeographicalNodeDTO> geographicalNodes, List<StreetDTO> streets) {
        this.geographicalNodes = geographicalNodes;
        this.streets = streets;
    }

    public List<StreetDTO> getStreets() {
        return streets;
    }

    public List<GeographicalNodeDTO> getGeographicalNodes() {
        return geographicalNodes;
    }
}
