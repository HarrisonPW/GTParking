package org.GTParking.dao;

import org.GTParking.entity.po.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {
    User queryById(Integer userid);


    List<User> queryAllByLimit(@Param("po")User sysUser, @Param("pageNo") Long pageNo, @Param("pageSize") Long pageSize);


    long count(User user);


    int insert(User user);


    int insertBatch(@Param("entities") List<User> entities);


    int insertOrUpdateBatch(@Param("entities") List<User> entities);


    int update(User user);


    int deleteById(Integer userid);
}

