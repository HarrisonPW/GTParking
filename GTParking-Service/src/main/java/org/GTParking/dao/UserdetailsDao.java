package org.GTParking.dao;

import org.GTParking.entity.po.Userdetails;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserdetailsDao {


    Userdetails queryById(Integer userid);


    List<Userdetails> queryAllByLimit(@Param("po")Userdetails userdetails, @Param("pageNo") Long pageNo, @Param("pageSize") Long pageSize);


    long count(Userdetails userdetails);

    int insert(Userdetails userdetails);


    int insertBatch(@Param("entities") List<Userdetails> entities);


    int insertOrUpdateBatch(@Param("entities") List<Userdetails> entities);


    int update(Userdetails userdetails);


    int deleteById(Integer userid);

}

