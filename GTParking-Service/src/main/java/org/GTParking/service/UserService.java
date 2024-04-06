package org.GTParking.service;

import org.GTParking.bean.PageResponse;
import org.GTParking.entity.po.User;
import org.GTParking.entity.request.UserRequest;

import java.util.Date;

public interface UserService {
    User queryUserVById(Integer userid);

    User insertUser(User user);

    User updateUser(User user);

    boolean deleteUserById(Integer userid);

//    True if there's an update to the checked in value, False if not.
    boolean updateLocation(User user, Date timestamp, Double latitude, Double longitude);
}