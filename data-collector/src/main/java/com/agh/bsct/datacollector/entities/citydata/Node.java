package com.agh.bsct.datacollector.entities.citydata;

public class Node {
    private Long id;

    private Double lon;
    private Double lat;

    public Node(Long id, Double lon, Double lat) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
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
}
