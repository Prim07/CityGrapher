package com.agh.bsct.datacollector.library.adapter.queryresult;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;

public class Element {

    @SerializedName("type")
    private String type;

    @SerializedName("id")
    private long id;

    @SerializedName("lat")
    private double lat;

    @SerializedName("lon")
    private double lon;

    @SerializedName("tags")
    private Tags tags = new Tags();

    @SerializedName("nodes")
    private long[] nodes;

    public String getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public Tags getTags() {
        return tags;
    }

    public long[] getNodes() {
        return nodes;
    }

    public ArrayList getNodesAsArrayList() {
        return new ArrayList<>(Arrays.asList(nodes));
    }
}
