package com.agh.bsct.datacollector;

import com.agh.bsct.datacollector.test.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//TODO AK restrict paths when comes more packages
@ComponentScan({"com.agh.bsct.datacollector"})
@EntityScan({"com.agh.bsct.datacollector"})
public class DataCollector {
    public static void main(String[] args) {
//        SpringApplication.run(DataCollector.class, args);
        Test test = new Test();
        test.run();
    }
}
