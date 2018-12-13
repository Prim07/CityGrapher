package com.agh.bsct.api.entities.citydata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CityDataDTO {

    @NotNull
    private List<GeographicalNodeDTO> geographicalNodes;

    @NotNull
    private List<StreetDTO> streets;

    public CityDataDTO(List<GeographicalNodeDTO> geographicalNodes, List<StreetDTO> streets) {
        this.geographicalNodes = geographicalNodes;
        this.streets = streets;
    }

}
