# Locus Assignment: SDE


### Running the application:
Dependencies: Java8+Maven
The user can use controller to get the coordinates using the following endpoint

FYI: The following is a GET request

`{BASE_URI}/directions?origin={startLat, startLong}&dest={endLat, endLog}`
Heroku: directions?origin={startLat, startLong}&dest={endLat, endLog}

Eg: `localhost:8080/directions?origin=12.93175, 77.62872&dest=12.92662, 77.63696`
Heroku Eg: https://maps-directions.herokuapp.com/directions?origin=12.93175,%2077.62872&dest=12.92662,%2077.63696
(Include request-id in header for traceability)

 -------
The project is distributed in three sections:
1) Configuring the maps SDK
2) Getting the route using Directions API
3) Playing with the legs.steps provided by google api to get the points


### 1 -  Configuring the Directions API

- Used JAVA SDK
- Setting up API: src/main/java/com/locus/maps/directions/config/GeoApiContextConfiguration.java

### 2 - Getting the route using Directions API

- Used standard Directions API as per Java SDK
- File: src/main/java/com/locus/maps/directions/services/MapService.java


### 3 - Getting the list of coordinates

- Used azimuth for calculating the bearing (for angling length)
- Used radian distancing to find points
- File: src/main/java/com/locus/maps/directions/services/DirectionService.java


### Items added for production readiness
- Used exception handling for Http Error code handling
- Used log4j for logging
- Configured RequestInterceptors along-with logback for logging request-id to all logs
- Added Zipkin libraries for distributed tracing in cloud.