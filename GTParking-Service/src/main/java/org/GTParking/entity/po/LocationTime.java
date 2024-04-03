package org.GTParking.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LocationTime implements Serializable {
    private Double longitude;
    private Double latitude;
    private Date timestamp;
}
