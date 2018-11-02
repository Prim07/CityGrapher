package com.agh.bsct.datacollector.entities.citydata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Street {

    private static final String NAME_TAG = "name";

    private List<Long> nodesIds;
    private Map<String, String> overpassTagNameToValue = new HashMap<>();


    public Street(String streetName, List<Long> nodesIds) {
        setName(streetName);
        this.nodesIds = nodesIds;
    }

    private void setName(String streetName) {
        overpassTagNameToValue.put(NAME_TAG, streetName);
    }

    public String getName() {
        return overpassTagNameToValue.get(NAME_TAG);
    }

    public List<Long> getNodesIds() {
        return nodesIds;
    }
}
