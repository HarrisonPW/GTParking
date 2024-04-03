package org.GTParking.service.impl;

import org.GTParking.dao.ParkinglotsDao;
import org.springframework.stereotype.Service;
import org.GTParking.bean.PageResponse;
import org.GTParking.entity.po.User;
import org.GTParking.dao.UserDao;
import org.GTParking.entity.request.UserRequest;
import org.GTParking.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service("UserService")
public class UserServiceImpl implements UserService{

    @Resource
    private UserDao userDao;

    @Override
    public User queryUserVById(Integer userid) {
        return this.userDao.queryById(userid);
    }

    @Override
    public PageResponse<User> queryUserByPage(UserRequest userRequest){

    }

    @Override
    public User insertUser(User user){
        this.userDao.insert(user);
        return user;
    }

    @Override
    public User updateUser(User user){
        this.userDao.update(user);
        return this.queryUserVById(user.getUserid());
    }

    @Override
    public boolean deleteById(Integer userid){
        return this.userDao.deleteById(userid) > 0;
    }

    @Override
    public boolean updateLocation(Integer userid, Date timestamp, Double latitude, Double longitude){

    }
}
