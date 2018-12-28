package com.agh.bsct.api.entities.taskinput;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class TaskInputDTO {

    @NotNull
    private String cityName;

    @NotNull
    private Integer numberOfResults;

    @NotNull
    private String algorithmType;

}
