package org.GTParking.service.impl;


import org.GTParking.dao.ParkinglotsDao;
import org.GTParking.entity.po.Parkinglots;
import org.GTParking.entity.po.LocationTime;
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
import java.util.ArrayList;
import java.util.Queue;

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
        User user = new User();
        BeanUtils.copyProperties(userRequest, user);
        PageResponse<User> pageResponse = new PageResponse<>();
        pageResponse.setCurrent(userRequest.getPageNo());
        pageResponse.setPageSize(userRequest.getPageSize());
        Long pageStart = (userRequest.getPageNo() - 1) * userRequest.getPageSize();
        long total = this.userDao.count(user);
        List<User> userList = this.userDao.queryAllByLimit(user, pageStart, userRequest.getPageSize());
        pageResponse.setTotal(total);
        pageResponse.setRecords(userList);
        return pageResponse;
    }

    @Override
    public User insertUser(User user){

        this.userDao.insert(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        this.userDao.update(user);
        return this.queryUserVById(user.getUserid());
    }

    @Override
    public boolean deleteUserById(Integer userid){
        return this.userDao.deleteById(userid) > 0;
    }

    @Override
    public User updateLocation(User user, Date timestamp, Double latitude, Double longitude) {
        ArrayList<LocationTime> path = user.getPath();
        LocationTime currLT = new LocationTime(longitude, latitude, timestamp);
        path.add(currLT);

//        remove outdated datapoints
        while (!path.isEmpty() && path.get(0).getTimestamp().before(new Date(timestamp.getTime() - 600000))) {
            path.remove(0);
        }
        updateDrivingStatus(user);
        return user;
    }

//    TODO: update user database
    private void updateDrivingStatus(User user) {
        ArrayList<LocationTime> path = user.getPath();
        Double speedThreshold = 3.0;
        if (path.size() <= 1) {
            return;
        }

//        in meters
        double totalDistance = 0;
//        in seconds
        double totalInterval = Math.abs(path.get(path.size()).getTimestamp().getTime() - path.get(0).getTimestamp().getTime()) / 1000;

        for (int i = 0; i < path.size() - 1; i++) {
            Double lat1 = path.get(i).getLatitude();
            Double lon1 = path.get(i).getLongitude();
            Double lat2 = path.get(i + 1).getLatitude();
            Double lon2 = path.get(i + 1).getLongitude();
            totalDistance += calculateDistance(lat1, lon1, lat2, lon2);
        }

        boolean isDriving = (totalDistance / totalInterval) > speedThreshold;
        boolean prevIsDriving = user.isDriving();
        user.setDriving(isDriving);

        updateCheckedin(user, isDriving, prevIsDriving);

    }

//    TODO: update user database
//    TODO: consider parking lot location
    private void updateCheckedin(User user, boolean isDriving, boolean prevIsDriving) {

        
    }

    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final double RADIUS_OF_EARTH = 6371000;
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.pow(Math.sin(deltaLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIUS_OF_EARTH * c;
    }
}
