package com.agh.bsct.datacollector.services;

import com.agh.bsct.datacollector.library.adapter.OverpassQueryResult;
import com.agh.bsct.datacollector.library.query.OverpassQuery;
import com.agh.bsct.datacollector.library.union.Recurse;
import com.agh.bsct.datacollector.library.union.output.OutputFormat;
import com.agh.bsct.datacollector.library.union.output.OutputOrder;
import com.agh.bsct.datacollector.library.union.output.OutputVerbosity;
import com.agh.bsct.datacollector.services.interpreter.QueryInterpreter;

//TODO AK autowire via constructor here
public class OSMCityService {

    private QueryInterpreter queryInterpreter;

    public OSMCityService() {
        this.queryInterpreter = new QueryInterpreter();
    }

    public OverpassQueryResult getCityData(String cityName) {
        String query = getQueryForCity(cityName);
        return queryInterpreter.interpret(query);

    }

    private String getQueryForCity(String cityName) {
        String wayTypes = getWayTypesForEveryCity();

        return new OverpassQuery()
                .format(OutputFormat.JSON)
                .filterQuery()
                .area().tag("name", cityName)
                .prepareNext()
                .way().tagLike("highway", wayTypes).forKey("area")
                .end()
                .output(OutputVerbosity.META)
                .output(Recurse.DOWN)
                .output(OutputVerbosity.SKEL, OutputOrder.QT)
                .build();
    }

    private String getWayTypesForEveryCity() {
        return new StringBuilder()
                .append("motorway").append("|")
                .append("trunk").append("|")
                .append("primary").append("|")
                .append("motorway_link").append("|")
                .append("trunk_link").append("|")
                .append("primary_link").append("|")
                .append("unclassified").append("|")
                .append("tertiary").append("|")
                .append("secondary").append("|")
                .append("track").append("|")
                .append("path").append("|")
                .append("residential").append("|")
                .append("service").append("|")
                .append("secondary_link").append("|")
                .append("tertiary_link")
                .toString();
    }

}
