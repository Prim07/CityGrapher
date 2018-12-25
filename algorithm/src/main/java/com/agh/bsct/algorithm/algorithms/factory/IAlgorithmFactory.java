package com.agh.bsct.algorithm.algorithms.factory;

import com.agh.bsct.algorithm.algorithms.BFAlgorithm;
import com.agh.bsct.algorithm.algorithms.IAlgorithm;
import com.agh.bsct.algorithm.algorithms.SAAlgorithm;
import com.agh.bsct.algorithm.algorithms.outputwriter.GnuplotOutputWriter;
import com.agh.bsct.algorithm.controllers.mapper.AlgorithmTaskMapper;
import com.agh.bsct.algorithm.services.algorithms.AlgorithmFunctionsService;
import com.agh.bsct.algorithm.services.algorithms.CrossingsService;
import com.agh.bsct.algorithm.services.entities.graph.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IAlgorithmFactory {

    private AlgorithmFunctionsService algorithmFunctionsService;
    private CrossingsService crossingsService;
    private AlgorithmTaskMapper algorithmTaskMapper;
    private GraphService graphService;
    private GnuplotOutputWriter gnuplotOutputWriter;

    @Autowired
    public IAlgorithmFactory(AlgorithmFunctionsService algorithmFunctionsService,
                             CrossingsService crossingsService,
                             AlgorithmTaskMapper algorithmTaskMapper,
                             GraphService graphService,
                             GnuplotOutputWriter gnuplotOutputWriter) {
        this.algorithmFunctionsService = algorithmFunctionsService;
        this.crossingsService = crossingsService;
        this.algorithmTaskMapper = algorithmTaskMapper;
        this.graphService = graphService;
        this.gnuplotOutputWriter = gnuplotOutputWriter;
    }

    public IAlgorithm getIAlgorithm(String algorithmType) {
        if ("sa".equals(algorithmType)) {
            return new SAAlgorithm(algorithmFunctionsService,
                    crossingsService,
                    algorithmTaskMapper,
                    graphService,
                    gnuplotOutputWriter);
        } else if ("bf".equals(algorithmType)) {
            return new BFAlgorithm(algorithmFunctionsService,
                    crossingsService,
                    algorithmTaskMapper,
                    graphService);
        } else {
            throw new IllegalStateException("Cannot create IAlgorithm with type: " + algorithmType);
        }
    }

}
