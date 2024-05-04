package com.rmacd.services;

import de.micromata.opengis.kml.v_2_2_0.*;
import jakarta.servlet.http.HttpServletResponse;
import org.geotools.referencing.GeodeticCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class LobService {

    private static final Logger logger = LoggerFactory.getLogger(LobService.class);

    @PostMapping(value = "/services/lob", produces = "application/vnd.google-earth.kml+xml")
//    @PostMapping(value = "/services/lob", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] getLob(
            @RequestParam String centre,
            @RequestParam String distance,
            @RequestParam String angle,
            HttpServletResponse response
    ) {
        double lat = Double.parseDouble(centre.split(",")[0]);
        double lon = Double.parseDouble(centre.split(",")[1]);

        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingGeographicPoint(lon, lat);
        calc.setDirection(Double.parseDouble(angle), Double.parseDouble(distance) * 1000);
        Point2D dest = calc.getDestinationGeographicPoint();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition
                        .attachment()
                        .filename("kml-tools-%s.kml".formatted(formatter.format(LocalDateTime.now())))
                        .build().toString());

        return getLine(lat, lon, dest.getY(), dest.getX()).getBytes(StandardCharsets.UTF_8);
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            kml.marshal(byteArrayOutputStream);
            return byteArrayOutputStream.toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to marshal");
        }
    }
}
