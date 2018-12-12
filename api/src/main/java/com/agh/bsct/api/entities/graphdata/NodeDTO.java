package com.agh.bsct.api.entities.graphdata;

import com.agh.bsct.api.entities.citydata.GeographicalNodeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class NodeDTO {

    @NotNull
    private GeographicalNodeDTO geographicalNodeDTO;

    @NotNull
    private Integer weight;

    public NodeDTO(GeographicalNodeDTO geographicalNodeDTO, Integer weight) {
        this.geographicalNodeDTO = geographicalNodeDTO;
        this.weight = weight;
    }

}
