package org.GTParking.dao;

import org.GTParking.entity.po.Parkinglots;
import org.GTParking.entity.request.QueryAllByAvailableSpotsRankingRequest;
import org.apache.ibatis.annotations.Param;
import java.util.List;


public interface ParkinglotsDao {


    Parkinglots queryById(Integer parkinglotid);


    List<Parkinglots> queryAllByLimit(@Param("po")Parkinglots sysUser, @Param("pageNo") Long pageNo, @Param("pageSize") Long pageSize);


    long count(Parkinglots parkinglots);


    int insert(Parkinglots parkinglots);


    int insertBatch(@Param("entities") List<Parkinglots> entities);


    int insertOrUpdateBatch(@Param("entities") List<Parkinglots> entities);


    int update(Parkinglots parkinglots);


    int deleteById(Integer parkinglotid);

    List<Parkinglots> queryAllByAvailableSpotsRanking(@Param("po") Parkinglots sysUser, @Param("pageNo") Long pageNo, @Param("pageSize") Long pageSize);

}

