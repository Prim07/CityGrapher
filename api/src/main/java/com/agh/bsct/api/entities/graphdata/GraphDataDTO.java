package com.agh.bsct.api.entities.graphdata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GraphDataDTO {

    @NotNull
    private List<EdgeDTO> edgeDTOS;

    @NotNull
    private List<NodeDTO> nodeDTOS;

    public GraphDataDTO(List<EdgeDTO> edgeDTOS, List<NodeDTO> nodeDTOS) {
        this.edgeDTOS = edgeDTOS;
        this.nodeDTOS = nodeDTOS;
    }
}
