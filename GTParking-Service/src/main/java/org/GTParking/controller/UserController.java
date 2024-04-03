package org.GTParking.controller;

import org.GTParking.bean.PageResponse;
import org.GTParking.bean.Result;
import org.GTParking.entity.po.User;
import org.GTParking.entity.request.UserRequest;
import org.GTParking.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("users")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping
    public Result<PageResponse<User>> queryByPage(UserRequest userRequest) {
        return Result.ok(this.userService.queryUserByPage(userRequest));
    }


    @GetMapping("{id}")
    public Result<User> queryById(@PathVariable("id") Integer id) {
        return Result.ok(this.userService.queryUserVById(id));
    }


    @PostMapping
    public Result<User> add(User user) {
        return Result.ok(this.userService.insertUser(user));
    }


    @PutMapping
    public Result<User> edit(User user) {
        return Result.ok(this.userService.updateUser(user));
    }


    @DeleteMapping
    public Result<Boolean> deleteById(Integer id) {
        return Result.ok(this.userService.deleteUserById(id));
    }

    public Result<Boolean> updateLocation(User userid, Date timestamp, Double latitude, Double longitude) {
        return Result.ok(this.userService.updateLocation(userid, timestamp, latitude, longitude));
    }

}
