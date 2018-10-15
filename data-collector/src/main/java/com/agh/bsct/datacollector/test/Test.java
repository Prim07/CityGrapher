package com.agh.bsct.datacollector.test;

import com.agh.bsct.datacollector.library.adapter.OverpassQueryResult;
import com.agh.bsct.datacollector.library.adapter.OverpassServiceProvider;
import com.agh.bsct.datacollector.library.output.OutputFormat;
import com.agh.bsct.datacollector.library.output.OutputModificator;
import com.agh.bsct.datacollector.library.output.OutputOrder;
import com.agh.bsct.datacollector.library.output.OutputVerbosity;
import com.agh.bsct.datacollector.library.query.OverpassQuery;

//TODO AK this is just simple class with example of how to query OSM API
public class Test {

    public void run() {
        String query = new OverpassQuery()
                .format(OutputFormat.JSON)
                .filterQuery()
                .node()
                .amenity("parking")
                .tagNot("access", "private")
                .boundingBox(
                        47.48047027491862, 19.039797484874725,
                        47.51331674014172, 19.07404761761427)
                .end()
                .output(OutputVerbosity.BODY, OutputModificator.CENTER, OutputOrder.QT, 100)
                .build();

        OverpassQueryResult interpret = interpret(query);
    }

    private OverpassQueryResult interpret(String query) {
        try {
            return OverpassServiceProvider.get().interpreter(query).execute().body();

        } catch (Exception e) {
            e.printStackTrace();

            return new OverpassQueryResult();
        }
    }
}
