package com.agh.bsct.datacollector;

import com.agh.bsct.datacollector.controllers.DataCollectorController;
import com.agh.bsct.datacollector.services.algorithm.boundary.AlgorithmService;
import com.agh.bsct.datacollector.services.city.OSMCityService;
import com.agh.bsct.datacollector.services.city.QueryForCityProvider;
import com.agh.bsct.datacollector.services.data.CityDataService;
import com.agh.bsct.datacollector.services.data.GraphDataService;
import com.agh.bsct.datacollector.services.interpreter.QueryInterpreterService;
import com.agh.bsct.datacollector.services.parser.DataParser;
import com.agh.bsct.datacollector.services.result.filter.ResultFilterService;
import com.agh.bsct.datacollector.services.result.joiner.StreetsJoinerService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.agh.bsct.datacollector.controllers", "com.agh.bsct.datacollector.services"})
public class DataCollector {
    public static void main(String[] args) {
        SpringApplication.run(DataCollector.class, args);

        //DataCollectorController dataCollectorController = new DataCollectorController(new OSMCityService(new GraphDataService(), new CityDataService(new QueryForCityProvider(), new QueryInterpreterService(), new ResultFilterService(), new StreetsJoinerService()), new AlgorithmService(), new DataParser()));
        //ObjectNode cityData = dataCollectorController.getCityGraph("Bochnia");
    }
}

