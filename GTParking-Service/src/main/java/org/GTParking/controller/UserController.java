package org.GTParking.controller;

import org.GTParking.bean.PageResponse;
import org.GTParking.bean.Result;
import org.GTParking.entity.po.User;
import org.GTParking.entity.request.UserRequest;
import org.GTParking.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    public Result<User> add(User parkinglots) {
        return Result.ok(this.userService.insertUser(parkinglots));
    }


    @PutMapping
    public Result<User> edit(User parkinglots) {
        return Result.ok(this.userService.updateUser(parkinglots));
    }


    @DeleteMapping
    public Result<Boolean> deleteById(Integer id) {
        return Result.ok(this.userService.deleteById(id));
    }

}
