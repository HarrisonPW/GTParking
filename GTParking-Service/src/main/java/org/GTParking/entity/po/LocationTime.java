package org.GTParking.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LocationTime implements Serializable {
    private Double longitude;
    private Double latitude;
    private Long timestamp;

    public LocationTime(Double longitude, Double latitude, Long timestamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = timestamp;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}