package com.locus.maps.directions.services;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.locus.maps.directions.exception.LocusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

@Service
public class MapService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GeoApiContext geoApiContext;

    public MapService(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }

    public DirectionsResult directionsResponse(LatLng start, LatLng end) throws LocusException {
        DirectionsResult directionsResult;
        logger.info("Requesting directions for origin = {} destination = {}", start.toString(), end.toString());
        try {
            directionsResult = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.DRIVING).origin(start)
                    .destination(end).departureTimeNow().await();
        } catch (ApiException | IOException e) {
            logger.error("Suspected Bad Input. Error: {}", e.getMessage());
            throw new LocusException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        } catch (InterruptedException e) {
            logger.error("Execution Interrupted. Error: {}", e.getMessage());
            throw new LocusException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
        return directionsResult;
    }
}
