package com.agh.bsct.view.controllers;

import com.agh.bsct.view.config.ApiKeyProvider;
import com.agh.bsct.view.controllers.factories.MarkerFactory;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.geocoding.GeocoderStatus;
import com.lynden.gmapsfx.service.geocoding.GeocodingResult;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MapViewController implements Initializable, MapComponentInitializedListener {

    @FXML
    private GoogleMapView mapView;

    @FXML
    private TextField addressTextField;

    private GoogleMap map;
    private GeocodingService geocodingService;
    private StringProperty address;

    private MarkerFactory markerFactory;

    public MapViewController() {
        this.markerFactory = new MarkerFactory();
        this.address = new SimpleStringProperty();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapView.setKey(ApiKeyProvider.getApiKey());
        mapView.addMapInializedListener(this);
        address.bind(addressTextField.textProperty());
    }

    @Override
    public void mapInitialized() {
        geocodingService = new GeocodingService();

        MapOptions mapOptions = new MapOptions();
        initializeMapProperties(mapOptions);

        map = mapView.createMap(mapOptions);

        Marker exampleMarker = markerFactory.getMarker(50.061696, 19.937398);
        map.addMarker(exampleMarker);
    }

    private void initializeMapProperties(MapOptions mapOptions) {
        mapOptions.center(new LatLong(50.061696, 19.937398))
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .zoom(12);
    }

    @FXML
    public void addressTextFieldAction() {
        geocodingService.geocode(address.get(), this::handleGeocodedResults);
    }

    private void handleGeocodedResults(GeocodingResult[] results, GeocoderStatus status) {
        if (status == GeocoderStatus.ZERO_RESULTS) {
            var alert = new Alert(Alert.AlertType.ERROR, "No matching address found");
            alert.show();
        } else if (results.length > 1) {
            var alert = new Alert(Alert.AlertType.WARNING, "Multiple results found, showing the first one.");
            alert.show();
            var latLong = new LatLong(results[0].getGeometry().getLocation().getLatitude(), results[0].getGeometry().getLocation().getLongitude());
            map.setCenter(latLong);
        } else {
            var latLong = new LatLong(results[0].getGeometry().getLocation().getLatitude(), results[0].getGeometry().getLocation().getLongitude());
            map.setCenter(latLong);
        }
    }
}
