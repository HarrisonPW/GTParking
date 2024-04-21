package org.GTParking.service.impl;

import org.GTParking.bean.PageResponse;
import org.GTParking.convert.ParkinglotConverter;
import org.GTParking.entity.po.Parkinglots;
import org.GTParking.dao.ParkinglotsDao;
import org.GTParking.entity.request.ParkinglotsRequest;
import org.GTParking.entity.request.QueryAllByAvailableSpotsRankingRequest;
import org.GTParking.service.ParkinglotsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("parkinglotsService")
public class ParkinglotsServiceImpl implements ParkinglotsService {
    @Resource
    private ParkinglotsDao parkinglotsDao;


    @Override
    public Parkinglots queryById(Integer parkinglotid) {
        return this.parkinglotsDao.queryById(parkinglotid);
    }


    @Override
    public PageResponse<Parkinglots> queryByPage(ParkinglotsRequest parkinglotsRequest) {
        Parkinglots parkinglots = new Parkinglots();
        BeanUtils.copyProperties(parkinglotsRequest, parkinglots);
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

    @Override
    public Parkinglots insert(Parkinglots parkinglots) {
        this.parkinglotsDao.insert(parkinglots);
        return parkinglots;
    }

    @Override
    public Parkinglots update(Parkinglots parkinglots) {
        this.parkinglotsDao.update(parkinglots);
        return this.queryById(parkinglots.getParkinglotid());
    }

    @Override
    public boolean deleteById(Integer parkinglotid) {
        return this.parkinglotsDao.deleteById(parkinglotid) > 0;
    }

    @Override
    public PageResponse<Parkinglots> queryAllByAvailableSpotsRanking(QueryAllByAvailableSpotsRankingRequest parkinglotsRequest) {
        Parkinglots parkinglots = new Parkinglots();
        BeanUtils.copyProperties(parkinglotsRequest, parkinglots);
        PageResponse<Parkinglots> pageResponse = new PageResponse<>();
        pageResponse.setCurrent(parkinglotsRequest.getPageNo());
        pageResponse.setPageSize(parkinglotsRequest.getPageSize());
        Long pageStart = (parkinglotsRequest.getPageNo() - 1) * parkinglotsRequest.getPageSize();
        long total = this.parkinglotsDao.count(parkinglots);
        List<Parkinglots> parkinglotsList = this.parkinglotsDao.queryAllByAvailableSpotsRanking(parkinglots, pageStart, parkinglotsRequest.getPageSize());
        pageResponse.setTotal(total);
        pageResponse.setRecords(parkinglotsList);
        return pageResponse;

    }
}
