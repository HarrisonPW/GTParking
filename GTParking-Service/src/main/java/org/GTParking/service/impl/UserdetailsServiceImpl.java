package org.GTParking.service.impl;

import org.GTParking.bean.PageResponse;
import org.GTParking.convert.UserdetailsConverter;
import org.GTParking.dao.UserdetailsDao;
import org.GTParking.entity.po.LocationTime;
import org.GTParking.entity.po.Parkinglots;
import org.GTParking.entity.po.Userdetails;
import org.GTParking.entity.request.ParkinglotsRequest;
import org.GTParking.entity.request.UpdateUserRequest;
import org.GTParking.entity.request.UserdetailsRequest;
import org.GTParking.service.ParkinglotsService;
import org.GTParking.service.UserdetailsService;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service("userdetailsService")
public class UserdetailsServiceImpl implements UserdetailsService {
    @Resource
    private UserdetailsDao userdetailsDao;
    @Resource
    private ParkinglotsService parkinglotsService;


    @Override
    public Userdetails queryById(Integer userid) {
        return this.userdetailsDao.queryById(userid);
    }


    @Override
    public PageResponse<Userdetails> queryByPage(UserdetailsRequest userdetailsRequest) {
        Userdetails userdetails = new Userdetails();
        BeanUtils.copyProperties(userdetailsRequest, userdetails);
        PageResponse<Userdetails> pageResponse = new PageResponse<>();
        pageResponse.setCurrent(userdetailsRequest.getPageNo());
        pageResponse.setPageSize(userdetailsRequest.getPageSize());
        Long pageStart = (userdetailsRequest.getPageNo() - 1) * userdetailsRequest.getPageSize();
        long total = this.userdetailsDao.count(userdetails);
        List<Userdetails> userdetailsList = this.userdetailsDao.queryAllByLimit(userdetails, pageStart, userdetailsRequest.getPageSize());
        pageResponse.setTotal(total);
        pageResponse.setRecords(userdetailsList);
        return pageResponse;
    }


    @Override
    public Userdetails insert(Userdetails userdetails) {
        this.userdetailsDao.insert(userdetails);
        return userdetails;
    }


    @Override
    public Userdetails update(Userdetails userdetails) {
        this.userdetailsDao.update(userdetails);
        return this.queryById(userdetails.getUserid());
    }

    @Override
    public boolean deleteById(Integer userid) {
        return this.userdetailsDao.deleteById(userid) > 0;
    }


    @Override
    public Boolean updateLocation(UpdateUserRequest userRequest, Double latitude, Double longitude) {
        Userdetails userTemp = new Userdetails();
        BeanUtils.copyProperties(userRequest, userTemp);
        List<Userdetails> userdetails = userdetailsDao.queryAllByLimit(userTemp, 0L, 100000L);
        Userdetails user = new Userdetails();
        if (userdetails.size() > 0) {
            user = userdetails.get(0);
        } else {
            user.setUsername(userRequest.getUsername());
            userdetailsDao.insert(user);
        }

        LocationTime currLT = new LocationTime(longitude, latitude, System.currentTimeMillis() / 1000);
        ArrayList<LocationTime> arrayList = new ArrayList<>();
        arrayList.add(currLT);
        ArrayList<LocationTime> path = new ArrayList<>();
        if (user.getPath().length() == 0) {
            path = arrayList;
        } else {
            path = convertPathStringToList(user.getPath() + ";" + convertPathListToString(arrayList));
        }

        while (!path.isEmpty() && currLT.getTimestamp() - path.get(0).getTimestamp() > 600) {
            path.remove(0);
        }
        user.setPath((convertPathListToString(path)));
        return updateDrivingStatus(user, path);
    }

    private Boolean updateDrivingStatus(Userdetails user, ArrayList<LocationTime> path) {
//        ArrayList<LocationTime> path = convertPathStringToList(user.getPath());
        Double speedThreshold = 2.0;
        if (path.size() <= 1) {
            userdetailsDao.update(user);
            return false;
        }

//        in meters
        double totalDistance = 0;
//        in seconds
        double totalInterval = Math.abs(path.get(path.size() - 1).getTimestamp() - path.get(0).getTimestamp());

        for (int i = 0; i < path.size() - 1; i++) {
            Double lat1 = path.get(i).getLatitude();
            Double lon1 = path.get(i).getLongitude();
            Double lat2 = path.get(i + 1).getLatitude();
            Double lon2 = path.get(i + 1).getLongitude();
            totalDistance += calculateDistance(lat1, lon1, lat2, lon2);
        }
        System.out.println(totalDistance);
        System.out.println(totalInterval);

        Integer isDriving = 0;
        if ((totalDistance / totalInterval) > speedThreshold) {
            isDriving = 1;
        }
        Integer prevIsDriving = user.getIsdriving();
        user.setIsdriving(isDriving);
        userdetailsDao.update(user);
        return false;
//        return updateCheckedin(user, isDriving, prevIsDriving, new ParkinglotsRequest());
    }

    //    TODO: edge case: user passes by both Parking Lot 1 and Parking Lot 2 within the window, cannot determine which parked at.
    private Boolean updateCheckedin(Userdetails user, Integer isDriving, Integer prevIsDriving, ParkinglotsRequest parkinglotsRequest) {
        if (isDriving == 1 && prevIsDriving == 1) {
            return false;
        }
        if (isDriving == 0 && prevIsDriving == 0) {
            return false;
        }

        Double parkinglotsRadius = 150.0;
        PageResponse<Parkinglots> parkinglotsPageResponse = parkinglotsService.queryByPage(parkinglotsRequest);
        List<Parkinglots> parkinglots = parkinglotsPageResponse.getResult();
        ArrayList<LocationTime> path = convertPathStringToList(user.getPath());

        for (int i = 0; i < parkinglots.size(); i++) {
            Double parkinglotsLat = parkinglots.get(i).getXCoordinate();
            Double parkinglotsLon = parkinglots.get(i).getYCoordinate();
            for (int j = 0; j < path.size(); j++) {
                Double userLat = path.get(j).getLatitude();
                Double userLon = path.get(j).getLongitude();
                if (calculateDistance(parkinglotsLat, parkinglotsLon, userLat, userLon) <= parkinglotsRadius) {
                    user.setIscheckedin(1 - user.getIscheckedin());
                    int currSpots = parkinglots.get(i).getAvailableSpots();
                    if (user.getIscheckedin() == 0) {
                        parkinglots.get(i).setAvailableSpots(currSpots + 1);
                    } else {
                        parkinglots.get(i).setAvailableSpots(currSpots - 1);
                    }
                    parkinglotsService.update(parkinglots.get(i));
                    userdetailsDao.update(user);
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

    private String convertPermitsListToString(ArrayList<String> permits) {
        if (permits == null || permits.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < permits.size(); i++) {
            result.append(permits.get(i));
            if (i < permits.size() - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    private ArrayList<String> convertPermitsStringToList(String permits) {
        ArrayList<String> resultList = new ArrayList<>();
        if (permits == null || permits.isEmpty()) {
            return resultList;
        }

        String[] parts = permits.split(",");
        resultList.addAll(Arrays.asList(parts));
        return resultList;
    }

    private String convertPathListToString(ArrayList<LocationTime> path) {
        StringBuilder result = new StringBuilder();
        for (LocationTime locationTime : path) {
            result.append(locationTime.getTimestamp()).append("?")
                    .append(locationTime.getLongitude()).append("?")
                    .append(locationTime.getLatitude()).append(";");
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    private ArrayList<LocationTime> convertPathStringToList(String path) {
        String[] locationStrings = path.split(";");
        ArrayList<LocationTime> locationTimes = new ArrayList<>();
        for (String locationString : locationStrings) {
            locationTimes.add(parseFromString(locationString));
        }
        return locationTimes;
    }

    private LocationTime parseFromString(String input) {
        String[] parts = input.split("\\?");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid input format for LocationTime");
        }
        Long timestamp = Long.parseLong(parts[0]);
        Double longitude = Double.parseDouble(parts[1]);
        Double latitude = Double.parseDouble(parts[2]);
        return new LocationTime(longitude, latitude, timestamp);
    }
}
