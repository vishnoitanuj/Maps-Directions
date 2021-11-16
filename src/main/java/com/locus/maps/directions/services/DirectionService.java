package com.locus.maps.directions.services;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;
import com.locus.maps.directions.exception.LocusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DirectionService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Long RADIUS_OF_EARTH = 6371000L;
    private final MapService mapService;

    public DirectionService(MapService mapService) {
        this.mapService = mapService;
    }

    public List<LatLng> getAllPoints(LatLng start, LatLng end, int interval) throws LocusException {
        DirectionsResult directionsResult = mapService.directionsResponse(start, end);
        if (!Optional.ofNullable(directionsResult.routes).isPresent()) {
            logger.error("No routes found for the co-ordinates");
            throw new LocusException("No routes found", HttpStatus.EXPECTATION_FAILED);
        }
        List<LatLng> latLngs = new ArrayList<>();
        DirectionsStep[] directionsSteps = directionsResult.routes[0].legs[0].steps;
        latLngs.add(start);
        long prevLeftDist = 0L;
        for (DirectionsStep step : directionsSteps) {
            if (prevLeftDist + step.distance.inMeters >= interval) {
                double azimuth = calculateBearing(step.startLocation, step.endLocation);
                if (prevLeftDist < interval) {
                    double gap = interval - prevLeftDist;
                    latLngs.add(getDestinationLatLong(step.startLocation, azimuth, gap));
                }
                long distance = getPathLength(latLngs.get(latLngs.size() - 1), step.endLocation);
                while (distance >= interval && getPathLength(latLngs.get(latLngs.size() - 1), step.endLocation) >= interval) {
                    azimuth = calculateBearing(latLngs.get(latLngs.size() - 1), step.endLocation);
                    latLngs.add(getDestinationLatLong(latLngs.get(latLngs.size() - 1), azimuth, interval));
                    distance = getPathLength(latLngs.get(latLngs.size() - 1), step.endLocation);
                }
                prevLeftDist = distance;
            } else
                prevLeftDist += step.distance.inMeters;
        }
        logger.info("Found {} points for startLocation = {} and endLocation = {}", latLngs.size(), start.toString(), end.toString());
        // Since asked points at interval between start and end, not including the ending point. If needed un-comment below statement
        // latLngs.add(end);
        return latLngs;
    }

    // calculates the distance between two lat, long coordinate pairs
    private long getPathLength(LatLng start, LatLng end) {
        double lat1Radian = Math.toRadians(start.lat);
        double lat2Radian = Math.toRadians(end.lat);
        double deltaLat = Math.toRadians((end.lat - start.lat));
        double deltaLng = Math.toRadians((end.lng - start.lng));
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(lat1Radian) * Math.cos(lat2Radian) * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (long) (RADIUS_OF_EARTH * c);
    }

    // calculates the azimuth in degrees from start point to end point
    private double calculateBearing(LatLng start, LatLng end) {
        double startLat = Math.toRadians(start.lat);
        double startLong = Math.toRadians(start.lng);
        double endLat = Math.toRadians(end.lat);
        double endLong = Math.toRadians(end.lng);
        double dLong = endLong - startLong;
        double dPhi = Math.log(Math.tan((endLat / 2.0) + (Math.PI / 4.0)) / Math.tan((startLat / 2.0) + (Math.PI / 4.0)));
        if (Math.abs(dLong) > Math.PI) {
            if (dLong > 0.0)
                dLong = -(2.0 * Math.PI - dLong);
            else
                dLong = (2.0 * Math.PI + dLong);
        }
        return ((Math.toDegrees(Math.atan2(dLong, dPhi)) + 360.0) % 360.0);
    }

    private LatLng getDestinationLatLong(LatLng latLng, double azimuth, double distance) {
        double radiusKm = RADIUS_OF_EARTH / 1000L; //Radius of the Earth in km
        double brng = Math.toRadians(azimuth);
        double d = distance / 1000;
        double lat1 = Math.toRadians(latLng.lat);
        double lon1 = Math.toRadians(latLng.lng);
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d / radiusKm) + Math.cos(lat1) * Math.sin(d / radiusKm) * Math.cos(brng));
        double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(d / radiusKm) * Math.cos(lat1), Math.cos(d / radiusKm) - Math.sin(lat1) * Math.sin(lat2));
        lat2 = Math.toDegrees(lat2);
        lon2 = Math.toDegrees(lon2);
        return new LatLng(lat2, lon2);
    }
}
