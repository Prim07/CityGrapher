package com.agh.bsct.datacollector.entities.citydata;

public class Node {
    private Long id;

    private Double lon;
    private Double lat;

    boolean isCrossing;

    public Node(Long id, Double lon, Double lat, boolean isCrossing) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        this.isCrossing = isCrossing;
    }

    public Node(Long id, Double lon, Double lat) {
        this(id, lon, lat, false);
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

    public void setCrossing(boolean crossing) {
        isCrossing = crossing;
    }

    public boolean isCrossing() {
        return isCrossing;
    }
}
