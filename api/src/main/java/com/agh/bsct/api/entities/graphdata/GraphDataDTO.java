package com.agh.bsct.api.entities.graphdata;

import java.util.List;

public class GraphDataDTO {

    private List<EdgeDTO> edgeDTOS;
    private List<NodeDTO> nodeDTOS;

    public GraphDataDTO(List<EdgeDTO> edgeDTOS, List<NodeDTO> nodeDTOS) {
        this.edgeDTOS = edgeDTOS;
        this.nodeDTOS = nodeDTOS;
    }

    public List<EdgeDTO> getEdgeDTOS() {
        return edgeDTOS;
    }

    public List<NodeDTO> getNodeDTOS() {
        return nodeDTOS;
    }
}
