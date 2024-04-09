package org.GTParking.service.impl;

import org.GTParking.bean.PageResponse;
import org.GTParking.convert.UserdetailsConverter;
import org.GTParking.dao.UserdetailsDao;
import org.GTParking.entity.po.Userdetails;
import org.GTParking.entity.request.UserdetailsRequest;
import org.GTParking.service.UserdetailsService;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service("userdetailsService")
public class UserdetailsServiceImpl implements UserdetailsService {
    @Resource
    private UserdetailsDao userdetailsDao;


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
}
