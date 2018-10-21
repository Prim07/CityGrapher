package com.agh.bsct.view.controllers.factories;

import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;

public class MarkerFactory {

    public Marker getMarker(double latitude, double longitude) {
        var latLong = new LatLong(latitude, longitude);
        var options = new MarkerOptions();
        options.position(latLong);
        return new Marker(options);
    }

}
