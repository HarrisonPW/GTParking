package org.GTParking.controller;

import org.GTParking.bean.PageResponse;
import org.GTParking.bean.Result;
import org.GTParking.entity.po.Parkinglots;
import org.GTParking.entity.po.Userdetails;
import org.GTParking.entity.request.UpdateUserRequest;
import org.GTParking.entity.request.UserdetailsRequest;
import org.GTParking.service.UserdetailsService;

import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("userdetails")
public class UserdetailsController {

    @Resource
    private UserdetailsService userdetailsService;


    @GetMapping
    public Result<PageResponse<Userdetails>> queryByPage(UserdetailsRequest userdetails) {
        return Result.ok(this.userdetailsService.queryByPage(userdetails));
    }


    @GetMapping("{id}")
    public Result<PageResponse<Userdetails>> queryById(@PathVariable("id") Integer id) {
        return Result.ok(this.userdetailsService.queryById(id));
    }


    @PostMapping
    public Result<Userdetails> add(Userdetails userdetails) {
        return Result.ok(this.userdetailsService.insert(userdetails));
    }


    @PutMapping
    public Result<Userdetails> edit(Userdetails userdetails) {
        return Result.ok(this.userdetailsService.update(userdetails));
    }

    @PutMapping("trackingAlgo")
    public Result<Userdetails> edit(UpdateUserRequest updateUserRequest, Double longitude, Double latitude) {
        return Result.ok(userdetailsService.updateLocation(updateUserRequest, latitude, longitude));
    }



    @DeleteMapping
    public Result<Boolean> deleteById(Integer id) {
        return Result.ok(this.userdetailsService.deleteById(id));
    }

}

