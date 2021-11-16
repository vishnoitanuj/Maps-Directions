package com.locus.maps.directions.api;

import com.google.maps.model.LatLng;
import com.locus.maps.directions.exception.LocusException;
import com.locus.maps.directions.services.DirectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DirectionsController {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DirectionService directionService;

    public DirectionsController(DirectionService directionService) {
        this.directionService = directionService;
    }

    @GetMapping("/directions")
    public ResponseEntity<List<String>> getPoints(@RequestParam("origin") String[] origin, @RequestParam("dest") String[] dest) throws LocusException {
        LatLng start = new LatLng(Double.parseDouble(origin[0]), Double.parseDouble(origin[1]));
        LatLng end = new LatLng(Double.parseDouble(dest[0]), Double.parseDouble(dest[1]));
        try {
            List<LatLng> latLngs = directionService.getAllPoints(start, end, 50);
            List<String> points = new ArrayList<>();
            for(LatLng latLng: latLngs) {
                points.add(latLng.toString());
            }
            return new ResponseEntity<>(points, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while extracting points. Error = {}", e.getMessage());
            throw new LocusException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
