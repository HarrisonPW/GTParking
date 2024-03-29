package org.GTParking.service;

import org.GTParking.bean.PageResponse;
import org.GTParking.entity.po.Parkinglots;
import org.GTParking.entity.request.ParkinglotsRequest;
import org.GTParking.entity.request.QueryAllByAvailableSpotsRankingRequest;


public interface ParkinglotsService {

    Parkinglots queryById(Integer parkinglotid);

    PageResponse<Parkinglots> queryByPage(ParkinglotsRequest parkinglotsRequest);


    Parkinglots insert(Parkinglots parkinglots);

    Parkinglots update(Parkinglots parkinglots);


    boolean deleteById(Integer parkinglotid);

    PageResponse<Parkinglots> queryAllByAvailableSpotsRanking(QueryAllByAvailableSpotsRankingRequest parkinglotsRequest);

}
