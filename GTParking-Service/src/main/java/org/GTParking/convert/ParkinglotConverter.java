package org.GTParking.convert;

import org.GTParking.entity.po.Parkinglots;
import org.GTParking.entity.request.ParkinglotsRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ParkinglotConverter {
    ParkinglotConverter INSTANCE = Mappers.getMapper(ParkinglotConverter.class);

    Parkinglots convertReqToParkinglot(ParkinglotsRequest parkinglotsRequest);
}
