package org.GTParking.convert;

import org.GTParking.entity.po.Userdetails;
import org.GTParking.entity.request.UserdetailsRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface UserdetailsConverter {
    UserdetailsConverter INSTANCE = Mappers.getMapper(UserdetailsConverter.class);

    Userdetails convertUserdetailsRequestToUserdetails(UserdetailsRequest userdetailsRequest);


}
