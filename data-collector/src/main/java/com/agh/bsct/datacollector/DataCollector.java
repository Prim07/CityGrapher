package com.agh.bsct.datacollector;

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

