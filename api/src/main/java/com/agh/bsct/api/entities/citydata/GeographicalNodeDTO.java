package com.agh.bsct.api.entities.citydata;

public class GeographicalNodeDTO {

    private Long id;
    private Double lon;
    private Double lat;
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

    public Long getId() {
        return id;
    }

    public Double getLon() {
        return lon;
    }

    public Double getLat() {
        return lat;
    }

    public boolean isCrossing() {
        return isCrossing;
    }

    public void setCrossing(boolean crossing) {
        isCrossing = crossing;
    }
}
