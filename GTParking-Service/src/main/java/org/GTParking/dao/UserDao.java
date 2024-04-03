package org.GTParking.dao;

import org.GTParking.entity.po.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {
    User queryById(Integer userid);

    int insert(User user);

    int update(User user);

    int deleteById(Integer userid);
}

