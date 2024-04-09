package org.GTParking.controller;

import org.GTParking.bean.PageResponse;
import org.GTParking.bean.Result;
import org.GTParking.entity.po.Parkinglots;
import org.GTParking.entity.request.ParkinglotsRequest;
import org.GTParking.entity.request.QueryAllByAvailableSpotsRankingRequest;
import org.GTParking.service.ParkinglotsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("parkinglots")
public class ParkinglotsController {

    @Resource
    private ParkinglotsService parkinglotsService;


    @GetMapping
    public Result<PageResponse<Parkinglots>> queryByPage(ParkinglotsRequest parkinglotsRequest) {
        return Result.ok(this.parkinglotsService.queryByPage(parkinglotsRequest));
    }

    @GetMapping("availableSpotsRanking")
    public Result<PageResponse<Parkinglots>> queryAllByAvailableSpotsRanking(QueryAllByAvailableSpotsRankingRequest queryAllByAvailableSpotsRankingRequest) {
        return Result.ok(this.parkinglotsService.queryAllByAvailableSpotsRanking(queryAllByAvailableSpotsRankingRequest));
    }


    @GetMapping("{id}")
    public Result<Parkinglots> queryById(@PathVariable("id") Integer id) {
        return Result.ok(this.parkinglotsService.queryById(id));
    }


    @PostMapping
    public Result<Parkinglots> add(Parkinglots parkinglots) {
        return Result.ok(this.parkinglotsService.insert(parkinglots));
    }


    @PutMapping
    public Result<Parkinglots> edit(Parkinglots parkinglots) {
        return Result.ok(this.parkinglotsService.update(parkinglots));
    }


    @DeleteMapping
    public Result<Boolean> deleteById(Integer id) {
        return Result.ok(this.parkinglotsService.deleteById(id));
    }

}

