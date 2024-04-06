package org.GTParking.service.impl;

import org.GTParking.bean.PageResponse;
import org.GTParking.dao.UserDao;
import org.GTParking.entity.po.LocationTime;
import org.GTParking.entity.po.Parkinglots;
import org.GTParking.entity.po.User;
import org.GTParking.entity.request.ParkinglotsRequest;
import org.GTParking.service.ParkinglotsService;
import org.GTParking.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("UserService")
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private ParkinglotsService parkinglotsService;
    @Resource
    private UserService userService;


    @Override
    public User queryUserVById(String userid) {
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
    public boolean deleteUserById(String userid) {
        return this.userDao.deleteById(userid) > 0;
    }

    @Override
    public boolean updateLocation(User user, Date timestamp, Double latitude, Double longitude) {
        ArrayList<LocationTime> path = user.getPath();
        LocationTime currLT = new LocationTime(longitude, latitude, timestamp);
        path.add(currLT);

        while (!path.isEmpty() && path.get(0).getTimestamp().before(new Date(timestamp.getTime() - 600000))) {
            path.remove(0);
        }

        return updateDrivingStatus(user);
    }

    private boolean updateDrivingStatus(User user) {
        ArrayList<LocationTime> path = user.getPath();
        Double speedThreshold = 3.0;
        if (path.size() <= 1) {
            return false;
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
        userService.updateUser(user);
        return updateCheckedin(user, isDriving, prevIsDriving, new ParkinglotsRequest());
    }

//    TODO: edge case: user passes by both Parking Lot 1 and Parking Lot 2 within the window, cannot determine which parked at.
    private boolean updateCheckedin(User user, boolean isDriving, boolean prevIsDriving, ParkinglotsRequest parkinglotsRequest) {
        if (isDriving && prevIsDriving) {
            return false;
        }
        if (!isDriving && !prevIsDriving) {
            return false;
        }

        Double parkinglotsRadius = 150.0;
        PageResponse<Parkinglots> parkinglotsPageResponse = parkinglotsService.queryByPage(parkinglotsRequest);
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
                    int currSpots = parkinglots.get(i).getAvailableSpots();
                    if (!user.isCheckedIn()) {
                        parkinglots.get(i).setAvailableSpots(currSpots + 1);
                    } else {
                        parkinglots.get(i).setAvailableSpots(currSpots - 1);
                    }
                    parkinglotsService.update(parkinglots.get(i));
                    userService.updateUser(user);
                    return true;
                }
            }
        }
        return false;
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
