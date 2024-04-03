package org.GTParking.service.impl;

import org.GTParking.dao.UserDao;
import org.GTParking.entity.po.Parkinglots;
import org.GTParking.entity.po.User;
import org.GTParking.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("UserService")
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Override
    public User queryUserVById(Integer userid) {
        return this.userDao.queryById(userid);
    }

    @Override
    public User insertUser(User user) {
        this.userDao.insert(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        this.userDao.update(user);
        return this.queryUserVById(user.getUserid());
    }

    @Override
    public boolean deleteUserById(Integer userid) {
        return this.userDao.deleteById(userid) > 0;
    }


}
