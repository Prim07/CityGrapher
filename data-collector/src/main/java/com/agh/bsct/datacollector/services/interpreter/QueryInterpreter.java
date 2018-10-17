package com.agh.bsct.datacollector.services.interpreter;

import com.agh.bsct.datacollector.library.adapter.OverpassQueryResult;
import com.agh.bsct.datacollector.library.adapter.OverpassServiceProvider;

import java.net.SocketTimeoutException;

public class QueryInterpreter {

    private Integer exceptionCounter = 3;

    public OverpassQueryResult interpret(String query) {
        try {
            return OverpassServiceProvider.get().interpreter(query).execute().body();

        } catch (SocketTimeoutException e) {
            EXCEPTION_COUNTER--;
            return (EXCEPTION_COUNTER > 0)
                    ? interpret(query)
                    : new OverpassQueryResult();
        } catch (Exception e) {
            e.printStackTrace();

            return new OverpassQueryResult();
        }
    }

}
