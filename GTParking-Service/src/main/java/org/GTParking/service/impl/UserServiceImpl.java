package org.GTParking.service.impl;


import org.GTParking.bean.PageResponse;
import org.GTParking.convert.ParkinglotConverter;
import org.GTParking.dao.ParkinglotsDao;
import org.GTParking.dao.UserDao;
import org.GTParking.entity.po.LocationTime;
import org.GTParking.entity.po.Parkinglots;
import org.GTParking.entity.po.LocationTime;
import org.springframework.stereotype.Service;
import org.GTParking.bean.PageResponse;
import org.GTParking.entity.po.User;
import org.GTParking.entity.request.ParkinglotsRequest;
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
    @Resource
    private ParkinglotsDao parkinglotsDao;


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

        while (!path.isEmpty() && path.get(0).getTimestamp().before(new Date(timestamp.getTime() - 600000))) {
            path.remove(0);
        }
        updateDrivingStatus(user);
        return user;
    }

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
        //    TODO: update user database with the latest isDriving status.

        updateCheckedin(user, isDriving, prevIsDriving, new ParkinglotsRequest());
    }

//    TODO: edge case: user passes by both Parking Lot 1 and Parking Lot 2 within the window, cannot determine which parked at.
    private void updateCheckedin(User user, boolean isDriving, boolean prevIsDriving, ParkinglotsRequest parkinglotsRequest) {
        if (isDriving && prevIsDriving) {
            return;
        }
        if (!isDriving && !prevIsDriving) {
            return;
        }

        Double parkinglotsRadius = 150.0;
        PageResponse<Parkinglots> parkinglotsPageResponse = queryParkinglotsByPage(parkinglotsRequest);
        List<Parkinglots> parkinglots = parkinglotsPageResponse.getResult();
        ArrayList<LocationTime> path = user.getPath();

        for (int i = 0; i < parkinglots.size(); i++) {
            Double parkinglotsLat = parkinglots.get(i).getXCoordinate();
            Double parkinglotsLon = parkinglots.get(i).getYCoordinate();
            for (int j = 0; j < path.size(); j++) {
                Double userLat = path.get(j).getLatitude();
                Double userLon = path.get(j).getLongitude();
                if (calculateDistance(parkinglotsLat, parkinglotsLon, userLat, userLon) <= parkinglotsRadius) {
                    user.setCheckedIn(!user.isCheckedIn());
                    //    TODO: update user database with the latest checkedin status.
                    return;
                }
            }
        }
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

    private PageResponse<Parkinglots> queryParkinglotsByPage(ParkinglotsRequest parkinglotsRequest) {
        Parkinglots parkinglots = ParkinglotConverter.INSTANCE.convertParkinglotsRequestToParkinglot(parkinglotsRequest);
        PageResponse<Parkinglots> pageResponse = new PageResponse<>();
        pageResponse.setCurrent(parkinglotsRequest.getPageNo());
        pageResponse.setPageSize(parkinglotsRequest.getPageSize());
        Long pageStart = (parkinglotsRequest.getPageNo() - 1) * parkinglotsRequest.getPageSize();
        long total = this.parkinglotsDao.count(parkinglots);
        List<Parkinglots> parkinglotsList = this.parkinglotsDao.queryAllByLimit(parkinglots, pageStart, parkinglotsRequest.getPageSize());
        pageResponse.setTotal(total);
        pageResponse.setRecords(parkinglotsList);
        return pageResponse;
    }
}
