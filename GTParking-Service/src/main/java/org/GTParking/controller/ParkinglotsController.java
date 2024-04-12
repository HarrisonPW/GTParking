package org.GTParking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.GTParking.RedisComponent;
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

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private ObjectMapper objectMapper;


    @GetMapping
    public Result<PageResponse<Parkinglots>> queryByPage(ParkinglotsRequest parkinglotsRequest) {
        try {
            // if all attributes from the request are null, return all parking lots
            if (parkinglotsRequest.getParkinglotid() == null && parkinglotsRequest.getName() == null && parkinglotsRequest.getLocation() == null
                    && parkinglotsRequest.getTotalspotsnum() == null && parkinglotsRequest.getCurrentspotsnum() == null
                    && parkinglotsRequest.getAvailableSpots() == null && parkinglotsRequest.getXCoordinate() == null
                    && parkinglotsRequest.getYCoordinate() == null && parkinglotsRequest.getPermit() == null) {
                Boolean flag = redisComponent.containsKey("parkinglots");
                if (flag) {
                    String parkinglots = (String) redisComponent.getValue("parkinglots");
                    PageResponse<Parkinglots> parkinglotsPageResponse = objectMapper.readValue(parkinglots, PageResponse.class);
                    return Result.ok(parkinglotsPageResponse);
                }
                redisComponent.setKey("parkinglots", objectMapper.writeValueAsString(this.parkinglotsService.queryByPage(parkinglotsRequest)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(this.parkinglotsService.queryByPage(parkinglotsRequest));
    }

    @GetMapping("availableSpotsRanking")
    public Result<PageResponse<Parkinglots>> queryAllByAvailableSpotsRanking(QueryAllByAvailableSpotsRankingRequest queryAllByAvailableSpotsRankingRequest) {
        try {
            // if all attributes from the request are null, return all parking lots
            if (queryAllByAvailableSpotsRankingRequest.getParkinglotid() == null && queryAllByAvailableSpotsRankingRequest.getName() == null && queryAllByAvailableSpotsRankingRequest.getLocation() == null
                    && queryAllByAvailableSpotsRankingRequest.getTotalspotsnum() == null && queryAllByAvailableSpotsRankingRequest.getCurrentspotsnum() == null
                    && queryAllByAvailableSpotsRankingRequest.getAvailableSpots() == null && queryAllByAvailableSpotsRankingRequest.getXCoordinate() == null
                    && queryAllByAvailableSpotsRankingRequest.getYCoordinate() == null && queryAllByAvailableSpotsRankingRequest.getPermit() == null) {
                Boolean flag = redisComponent.containsKey("parkinglotsRanking");
                if (flag) {
                    String parkinglotsRanking = (String) redisComponent.getValue("parkinglotsRanking");
                    PageResponse<Parkinglots> parkinglotsPageResponse = objectMapper.readValue(parkinglotsRanking, PageResponse.class);
                    return Result.ok(parkinglotsPageResponse);
                }
                redisComponent.setKey("parkinglotsRanking", objectMapper.writeValueAsString(this.parkinglotsService.queryAllByAvailableSpotsRanking(queryAllByAvailableSpotsRankingRequest)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(this.parkinglotsService.queryAllByAvailableSpotsRanking(queryAllByAvailableSpotsRankingRequest));
    }


    @GetMapping("{id}")
    public Result<Parkinglots> queryById(@PathVariable("id") Integer id) {
        return Result.ok(this.parkinglotsService.queryById(id));
    }


    @PostMapping
    public Result<Parkinglots> add(Parkinglots parkinglots) {
        redisComponent.deleteKey("parkinglots");
        redisComponent.deleteKey("parkinglotsRanking");
        return Result.ok(this.parkinglotsService.insert(parkinglots));
    }


    @PutMapping
    public Result<Parkinglots> edit(Parkinglots parkinglots) {
        redisComponent.deleteKey("parkinglots");
        redisComponent.deleteKey("parkinglotsRanking");
        return Result.ok(this.parkinglotsService.update(parkinglots));
    }


    @DeleteMapping
    public Result<Boolean> deleteById(Integer id) {
        redisComponent.deleteKey("parkinglots");
        redisComponent.deleteKey("parkinglotsRanking");
        return Result.ok(this.parkinglotsService.deleteById(id));
    }

}

