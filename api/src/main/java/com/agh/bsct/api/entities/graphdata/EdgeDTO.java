package com.agh.bsct.api.entities.graphdata;

import com.agh.bsct.api.entities.citydata.StreetDTO;

public class EdgeDTO {

    private StreetDTO streetDTO;
    private double weight;

    public EdgeDTO(StreetDTO streetDTO, double weight) {
        this.streetDTO = streetDTO;
        this.weight = weight;
    }

    public StreetDTO getStreetDTO() {
        return streetDTO;
    }

    public double getWeight() {
        return weight;
    }
}
