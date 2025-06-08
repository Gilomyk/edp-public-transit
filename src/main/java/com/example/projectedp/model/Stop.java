package com.example.projectedp.model;

public class Stop {
    private final String id;
    private final String name;
    private final double latitude;
    private final double longitude;
    private String stopNumber;

    public Stop(String id, String name, double latitude, double longitude, String stopNumber) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.stopNumber = stopNumber;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getStopNumber() {
        return stopNumber;
    }

    @Override
    public String toString() {
        return name;
    }
}
