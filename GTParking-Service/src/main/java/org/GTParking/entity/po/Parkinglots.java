package org.GTParking.entity.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class Parkinglots implements Serializable {
    private static final long serialVersionUID = 7602052318242025807L;

    private Integer parkinglotid;

    private String name;

    private String location;

    private Integer totalspotsnum;

    private Integer currentspotsnum;

    private Integer availableSpots;

    private Double XCoordinate;

    private Double YCoordinate;

    private String permit;

}

