package com.agh.bsct.algorithm.services.runner.asyncrunner.config;

import com.agh.bsct.algorithm.algorithms.BFAlgorithm;
import com.agh.bsct.algorithm.algorithms.IAlgorithm;
import com.agh.bsct.algorithm.algorithms.SAAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

@Configuration
public class IAlgorithmConfig {

    @Autowired
    private SAAlgorithm saAlgorithm;

    @Autowired
    private BFAlgorithm bfAlgorithm;

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public IAlgorithm iAlgorithm(String algorithm) {
        if ("sa".equals(algorithm)) {
            return saAlgorithm;
        } else if ("bf".equals(algorithm)) {
            return bfAlgorithm;
        } else {
            throw new IllegalStateException("Cannot create IAlgorithm bean");
        }
    }
}
