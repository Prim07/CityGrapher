package com.agh.bsct.api.entities.graphdata;

import com.agh.bsct.api.entities.citydata.StreetDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class EdgeDTO {

    @NotNull
    private StreetDTO streetDTO;

    @NotNull
    private double weight;

    public EdgeDTO(StreetDTO streetDTO, double weight) {
        this.streetDTO = streetDTO;
        this.weight = weight;
    }

}
