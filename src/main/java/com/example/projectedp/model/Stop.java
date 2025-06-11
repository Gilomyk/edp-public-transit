package com.example.projectedp.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        Stop other = (Stop) obj;

        return new EqualsBuilder()
                .append(id, other.id)
                .append(stopNumber, other.stopNumber)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(stopNumber)
                .toHashCode();
    }
}
