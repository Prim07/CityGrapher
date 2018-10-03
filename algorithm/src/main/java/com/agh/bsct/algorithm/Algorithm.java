package com.agh.bsct.algorithm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//TODO AK restrict paths when comes more packages
@ComponentScan({"com.agh.bsct.algorithm"})
@EntityScan({"com.agh.bsct.algorithm"})
public class Algorithm {
    public static void main(String[] args) {
        SpringApplication.run(Algorithm.class, args);
    }

}
