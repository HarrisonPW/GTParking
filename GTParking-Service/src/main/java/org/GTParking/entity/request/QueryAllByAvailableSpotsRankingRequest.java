package org.GTParking.entity.request;

import lombok.Data;
import org.GTParking.bean.PageRequest;

import java.io.Serializable;

@Data
public class QueryAllByAvailableSpotsRankingRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = -6811476043310765159L;

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
