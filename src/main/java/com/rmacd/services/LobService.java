package com.rmacd.services;

import de.micromata.opengis.kml.v_2_2_0.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.geotools.referencing.GeodeticCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@Validated
public class LobService {

    private static final Logger logger = LoggerFactory.getLogger(LobService.class);

    @PostMapping(value = "/services/lob", produces = "application/vnd.google-earth.kml+xml")
    @ResponseBody
    @Valid
    public byte[] getLob(
            @RequestParam @Pattern(
                    regexp = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)," +
                             "\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$",
                    message = "Co-ordinates must be decimal"
            ) String centre,
            @RequestParam
            @Min(value = 0, message = "Distance cannot be less than 0km")
            @Max(value = 10000, message = "Distance cannot be greater than 10000km")
            Double distance,
            @RequestParam
            @Min(value = 0, message = "Angle cannot be less than 0&deg;")
            @Max(value = 360, message = "Angle cannot be greater than 360&deg;")
            String angle,
            HttpServletResponse response
    ) {
        double lat = Double.parseDouble(centre.split(",")[0]);
        double lon = Double.parseDouble(centre.split(",")[1]);

        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingGeographicPoint(lon, lat);
        calc.setDirection(Double.parseDouble(angle), distance * 1000);
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
