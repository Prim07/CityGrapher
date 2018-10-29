package com.agh.bsct.view.controllers.factories;

import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;

public class PolylineFactory {

    public Polyline getPolyline(double latitude1, double longitude1, double latitude2, double longitude2) {
        MVCArray path = new MVCArray();
        path.push(new LatLong(latitude1, longitude1));
        path.push(new LatLong(latitude2, longitude2));

        PolylineOptions polylineOptions = new PolylineOptions()
                .path(path)
                .strokeColor("#013ADF");

        return new Polyline(polylineOptions);
    }
}
