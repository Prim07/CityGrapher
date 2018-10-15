package com.agh.bsct.datacollector.services.interpreter;

import com.agh.bsct.datacollector.library.adapter.OverpassQueryResult;
import com.agh.bsct.datacollector.library.adapter.OverpassServiceProvider;

public class QueryInterpreter {

    public OverpassQueryResult interpret(String query) {
        try {
            return OverpassServiceProvider.get().interpreter(query).execute().body();

        } catch (Exception e) {
            e.printStackTrace();

            return new OverpassQueryResult();
        }
    }

}
