package com.agh.bsct.api.entities.citydata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class StreetDTO {

    @JsonIgnore
    private static final String NAME_TAG = "name";

    @NotNull
    private List<Long> nodesIds;

    @NotNull
    private Map<String, String> overpassTagNameToValue = new HashMap<>();

    public StreetDTO(String streetName, List<Long> nodesIds) {
        setName(streetName);
        this.nodesIds = nodesIds;
    }

    private void setName(String streetName) {
        overpassTagNameToValue.put(NAME_TAG, streetName);
    }

    public String getName() {
        return overpassTagNameToValue.get(NAME_TAG);
    }

}
