package org.GTParking.service;

import org.GTParking.bean.PageResponse;
import org.GTParking.entity.po.User;
import org.GTParking.entity.request.UserRequest;

import java.util.Date;

public interface UserService {
    User queryUserVById(Integer userid);

    PageResponse<User> queryUserByPage(UserRequest userRequest);

    User insertUser(User user);

    User updateUser(User user);

    boolean deleteById(Integer userid);

    boolean updateLocation(Integer userid, Date timestamp, Double latitude, Double longitude);
}