package com.agh.bsct.datacollector.services.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

@Service
public class GraphParser {

    //TODO simple example, build here JSON object representing graph
    public ObjectNode parseToJson() {
        return new ObjectMapper().createObjectNode()
                .put("aaa", "aaa")
                .put("bbb", "bbb")
                .put("ccc", "ccc");
    }

}
