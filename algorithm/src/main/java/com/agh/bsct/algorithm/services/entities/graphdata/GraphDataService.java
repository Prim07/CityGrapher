package com.agh.bsct.algorithm.services.entities.graphdata;

import com.agh.bsct.algorithm.entities.graph.GraphEdge;
import com.agh.bsct.algorithm.entities.graph.GraphNode;
import com.agh.bsct.algorithm.services.entities.graph.GraphService;
import com.agh.bsct.api.entities.graphdata.EdgeDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GraphDataService {

    private GraphService graphService;

    @Autowired
    public GraphDataService(GraphService graphService) {
        this.graphService = graphService;
    }

    public void replaceGraphWithItsBiggestCommonComponent(GraphDataDTO graphData,
                                                          Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap) {
        var biggestConnectedComponentNodes = graphService.findBiggestConnectedComponent(nodeToEdgesIncidenceMap);

        //we don't have to remove anything from nodes collection because we want to draw only edges
        var edges = graphData.getEdgeDTOS();
        edges.removeIf(edge -> !shouldEdgeBeKept(edge, biggestConnectedComponentNodes));
    }

    private boolean shouldEdgeBeKept(EdgeDTO edge, List<GraphNode> biggestConnectedComponentNodes) {
        List<Long> nodesIds = edge.getStreetDTO().getNodesIds();
        return shouldNodeBeKept(nodesIds.get(0), biggestConnectedComponentNodes)
                && shouldNodeBeKept(nodesIds.get(nodesIds.size() - 1), biggestConnectedComponentNodes);
    }

    private boolean shouldNodeBeKept(Long id, List<GraphNode> biggestConnectedComponentNodes) {
        return biggestConnectedComponentNodes.stream().anyMatch(graphNode -> graphNode.getId().equals(id));
    }
}
