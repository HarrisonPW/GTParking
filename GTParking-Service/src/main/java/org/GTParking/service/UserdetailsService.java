package org.GTParking.service;


import org.GTParking.bean.PageResponse;
import org.GTParking.entity.po.Parkinglots;
import org.GTParking.entity.po.Userdetails;
import org.GTParking.entity.request.ParkinglotsRequest;
import org.GTParking.entity.request.UserdetailsRequest;

public interface UserdetailsService {


    Userdetails queryById(Integer id);

    PageResponse<Userdetails> queryByPage(UserdetailsRequest userdetails);

    Userdetails insert(Userdetails userdetails);

    Userdetails update(Userdetails userdetails);

    boolean deleteById(Integer userid);

}
