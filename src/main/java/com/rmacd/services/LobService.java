package com.rmacd.services;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.geotools.referencing.GeodeticCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

@Controller
public class LobService {

    private static final Logger logger = LoggerFactory.getLogger(LobService.class);

    @PostMapping("/services/lob")
    @ResponseBody
    public String getLob(
            @RequestParam String centre,
            @RequestParam String distance,
            @RequestParam String angle
    ) {
        double lat = Double.parseDouble(centre.split(",")[0]);
        double lon = Double.parseDouble(centre.split(",")[1]);

        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingGeographicPoint(lon, lat);
        calc.setDirection(Double.parseDouble(angle), Double.parseDouble(distance) * 1000);
        Point2D dest = calc.getDestinationGeographicPoint();

        return getLine(lat, lon, dest.getY(), dest.getX());
    }


    String getLine(Double startLat, Double startLon, Double endLat, Double endLon) {
        Kml kml = KmlFactory.createKml();
        Document document = kml.createAndSetDocument();
        Placemark pm = document.createAndAddPlacemark();
        LineString ls = pm.createAndSetLineString()
                .addToCoordinates(startLon, startLat, 0)
                .addToCoordinates(endLon, endLat, 0)
                .withAltitudeMode(AltitudeMode.CLAMP_TO_GROUND);
        pm.createAndAddStyle().setLineStyle(new LineStyle().withColor("#FF0000FF").withWidth(5));
//        pm.createAndSetLookAt()
//                .withLongitude(startLon)
//                .withLatitude(startLat)
//                .withRange(100)
//                .withTilt(0);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            kml.marshal(byteArrayOutputStream);
            return byteArrayOutputStream.toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to marshal");
        }
    }
}
