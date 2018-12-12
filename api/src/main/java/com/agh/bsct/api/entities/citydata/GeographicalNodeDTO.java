package com.agh.bsct.api.entities.citydata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class GeographicalNodeDTO {

    @NotNull
    private Long id;

    @NotNull
    private Double lon;

    @NotNull
    private Double lat;

    @NotNull
    private boolean isCrossing;

    public GeographicalNodeDTO(Long id, Double lon, Double lat) {
        this(id, lon, lat, false);
    }

    private GeographicalNodeDTO(Long id, Double lon, Double lat, boolean isCrossing) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        this.isCrossing = isCrossing;
    }

}
